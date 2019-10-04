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

import java.io.Reader;
import java.util.Scanner;

import org.jooq.Configuration;
import org.jooq.DSLContext;
import org.jooq.Meta;
import org.jooq.MetaProvider;
import org.jooq.Queries;
import org.jooq.Query;
import org.jooq.Source;
import org.jooq.tools.JooqLogger;

/**
 * {@link MetaProvider} implementation which can {@link MetaProvider#provide()
 * provide} a {@link Meta} implementation based on a set of DDL scripts as the
 * input.
 * <p>
 * In contrast to {@link DDLMetaProvider} this implementation interprets the DDL
 * scripts.
 *
 * @author Knut Wannheden
 */
final class DDLInterpreterMetaProvider implements MetaProvider {

    private static final JooqLogger log = JooqLogger.getLogger(DDLInterpreterMetaProvider.class);

    private final Configuration     configuration;
    private final Source[]          scripts;

    public DDLInterpreterMetaProvider(Configuration configuration, Source... scripts) {
        this.configuration = configuration == null ? new DefaultConfiguration() : configuration;
        this.scripts = scripts;
    }

    @Override
    public Meta provide() {
        final DDLInterpreter interpreter = new DDLInterpreter();
        Configuration localConfiguration = configuration.derive();
        DSLContext ctx = DSL.using(localConfiguration);
        for (Source script : scripts)
            loadScript(ctx,  script, interpreter);

        return interpreter.meta();
    }

    private final void loadScript(DSLContext ctx, Source source, DDLInterpreter interpreter) {
        Reader reader = source.reader();
        try {
            Scanner s = new Scanner(reader).useDelimiter("\\A");
            Queries queries = ctx.parser().parse(s.hasNext() ? s.next() : "");

            for (Query query : queries) {
                interpreter.accept(query);
                log.info(query);
            }
        }
        catch (ParserException e) {
            log.error("An exception occurred while parsing a DDL script: " + e.getMessage()
                + ". Please report this error to https://github.com/jOOQ/jOOQ/issues/new", e);
            throw e;
        }
        finally {
            if (reader != null)
                try {
                    reader.close();
                }
                catch (Exception ignore) {}
        }
    }

}
