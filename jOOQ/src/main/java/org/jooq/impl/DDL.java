/*
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *  http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 * Other licenses:
 * -----------------------------------------------------------------------------
 * Commercial licenses for this work are available. These replace the above
 * ASL 2.0 and offer limited warranties, support, maintenance, and commercial
 * database integrations.
 *
 * For more information, please visit: http://www.jooq.org/licenses
 *
 *
 *
 *
 *
 *
 *
 *
 *
 *
 *
 *
 *
 *
 *
 *
 */
package org.jooq.impl;

import static org.jooq.DDLFlag.COMMENT;
import static org.jooq.DDLFlag.FOREIGN_KEY;
import static org.jooq.DDLFlag.PRIMARY_KEY;
import static org.jooq.DDLFlag.SCHEMA;
import static org.jooq.DDLFlag.SEQUENCE;
import static org.jooq.DDLFlag.TABLE;
import static org.jooq.DDLFlag.UNIQUE;
import static org.jooq.impl.DSL.constraint;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.jooq.Catalog;
import org.jooq.Constraint;
import org.jooq.DDLExportConfiguration;
import org.jooq.DSLContext;
import org.jooq.Field;
import org.jooq.ForeignKey;
import org.jooq.Queries;
import org.jooq.Query;
import org.jooq.Schema;
import org.jooq.Sequence;
import org.jooq.Table;
import org.jooq.UniqueKey;
import org.jooq.tools.StringUtils;

/**
 * @author Lukas Eder
 */
final class DDL {

    private final DSLContext             ctx;
    private final DDLExportConfiguration configuration;

    DDL(DSLContext ctx, DDLExportConfiguration configuration) {
        this.ctx = ctx;
        this.configuration = configuration;
    }

    private final Query createTable(Table<?> table, Collection<? extends Constraint> constraints) {
        return (configuration.createTableIfNotExists()
                    ? ctx.createTableIfNotExists(table)
                    : ctx.createTable(table))
                  .columns(table.fields())
                  .constraints(constraints);
    }

    private final Query createSequence(Sequence<?> sequence) {
        return configuration.createSequenceIfNotExists()
                    ? ctx.createSequenceIfNotExists(sequence)
                    : ctx.createSequence(sequence);
    }

    private final Query createTable(Table<?> table) {
        return createTable(table, constraints(table));
    }

    private final List<Query> alterTableAddConstraints(Table<?> table) {
        List<Constraint> constraints = constraints(table);
        List<Query> result = new ArrayList<>(constraints.size());

        for (Constraint constraint : constraints)
            result.add(ctx.alterTable(table).add(constraint));

        return result;
    }

    private final List<Constraint> constraints(Table<?> table) {
        List<Constraint> result = new ArrayList<>();

        result.addAll(primaryKeys(table));
        result.addAll(uniqueKeys(table));
        result.addAll(foreignKeys(table));

        return result;
    }

    private final List<Constraint> primaryKeys(Table<?> table) {
        List<Constraint> result = new ArrayList<>();

        if (configuration.flags().contains(PRIMARY_KEY))
            for (UniqueKey<?> key : table.getKeys())
                if (key.isPrimary())
                    result.add(constraint(key.getName()).primaryKey(key.getFieldsArray()));

        return result;
    }

    private final List<Constraint> uniqueKeys(Table<?> table) {
        List<Constraint> result = new ArrayList<>();

        if (configuration.flags().contains(UNIQUE))
            for (UniqueKey<?> key : table.getKeys())
                if (!key.isPrimary())
                    result.add(constraint(key.getName()).unique(key.getFieldsArray()));

        return result;
    }

    private final List<Constraint> foreignKeys(Table<?> table) {
        List<Constraint> result = new ArrayList<>();

        if (configuration.flags().contains(FOREIGN_KEY))
            for (ForeignKey<?, ?> key : table.getReferences())
                result.add(constraint(key.getName()).foreignKey(key.getFieldsArray()).references(key.getKey().getTable(), key.getKey().getFieldsArray()));

        return result;
    }

    final Queries queries(Table<?>... tables) {
        List<Query> queries = new ArrayList<>();

        for (Table<?> table : tables) {
            if (configuration.flags().contains(TABLE))
                queries.add(createTable(table));
            else
                queries.addAll(alterTableAddConstraints(table));

            queries.addAll(commentOn(table));
        }

        return ctx.queries(queries);
    }

    private final List<Query> commentOn(Table<?> table) {
        List<Query> result = new ArrayList<>();

        if (configuration.flags().contains(COMMENT)) {
            String tComment = table.getComment();

            if (!StringUtils.isEmpty(tComment))
                result.add(ctx.commentOnTable(table).is(tComment));

            for (Field<?> field : table.fields()) {
                String fComment = field.getComment();

                if (!StringUtils.isEmpty(fComment))
                    result.add(ctx.commentOnColumn(field).is(fComment));
            }
        }

        return result;
    }

    final Queries queries(Schema... schemas) {
        List<Query> queries = new ArrayList<>();

        for (Schema schema : schemas)
            if (configuration.flags().contains(SCHEMA) && !StringUtils.isBlank(schema.getName()))
                if (configuration.createSchemaIfNotExists())
                    queries.add(ctx.createSchemaIfNotExists(schema.getName()));
                else
                    queries.add(ctx.createSchema(schema.getName()));

        if (configuration.flags().contains(TABLE)) {
            for (Schema schema : schemas) {
                for (Table<?> table : schema.getTables()) {
                    List<Constraint> constraints = new ArrayList<>();

                    constraints.addAll(primaryKeys(table));
                    constraints.addAll(uniqueKeys(table));

                    queries.add(createTable(table, constraints));
                }
            }
        }
        else {
            for (Schema schema : schemas) {
                if (configuration.flags().contains(PRIMARY_KEY))
                    for (Table<?> table : schema.getTables())
                        for (Constraint constraint : primaryKeys(table))
                            queries.add(ctx.alterTable(table).add(constraint));

                if (configuration.flags().contains(UNIQUE))
                    for (Table<?> table : schema.getTables())
                        for (Constraint constraint : uniqueKeys(table))
                            queries.add(ctx.alterTable(table).add(constraint));
            }
        }

        if (configuration.flags().contains(FOREIGN_KEY))
            for (Schema schema : schemas)
                for (Table<?> table : schema.getTables())
                    for (Constraint constraint : foreignKeys(table))
                        queries.add(ctx.alterTable(table).add(constraint));

        if (configuration.flags().contains(SEQUENCE))
            for (Schema schema : schemas)
                for (Sequence<?> sequence : schema.getSequences())
                    queries.add(createSequence(sequence));

        if (configuration.flags().contains(COMMENT))
            for (Schema schema : schemas)
                for (Table<?> table : schema.getTables())
                    queries.addAll(commentOn(table));

        return ctx.queries(queries);
    }

    final Queries queries(Catalog catalog) {
        return queries(catalog.getSchemas().toArray(Tools.EMPTY_SCHEMA));
    }
}
