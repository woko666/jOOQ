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

package org.jooq.meta.mariadb;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import org.jooq.DSLContext;
import org.jooq.Record;
import org.jooq.SQLDialect;
import org.jooq.impl.DSL;
import org.jooq.meta.DefaultDataTypeDefinition;
import org.jooq.meta.DefaultSequenceDefinition;
import org.jooq.meta.SchemaDefinition;
import org.jooq.meta.SequenceDefinition;
import org.jooq.meta.mysql.MySQLDatabase;
import org.jooq.meta.mysql.information_schema.tables.Tables;
import org.jooq.util.mariadb.MariaDBDataType;

/**
 * @author Lukas Eder
 */
public class MariaDBDatabase extends MySQLDatabase {

    @Override
    protected DSLContext create0() {
        return DSL.using(getConnection(), SQLDialect.MARIADB);
    }

    @Override
    protected List<SequenceDefinition> getSequences0() throws SQLException {
        List<SequenceDefinition> result = new ArrayList<>();

        for (Record record : create()
                .select(Tables.TABLE_SCHEMA, Tables.TABLE_NAME)
                .from(Tables.TABLES)
                .where(Tables.TABLE_TYPE.eq("SEQUENCE"))) {

            SchemaDefinition schema = getSchema(record.get(Tables.TABLE_SCHEMA));
            if (schema != null) {
                String name = record.get(Tables.TABLE_NAME);

                DefaultDataTypeDefinition type = new DefaultDataTypeDefinition(
                    this,
                    schema,
                    MariaDBDataType.BIGINT.getTypeName()
                );

                result.add(new DefaultSequenceDefinition(schema, name, type));
            }
        }

        return result;
    }
}
