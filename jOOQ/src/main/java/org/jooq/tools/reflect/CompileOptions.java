/*
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.jooq.tools.reflect;

import java.util.Arrays;



import java.util.Collections;
import java.util.List;

import javax.annotation.processing.Processor;

/**
 * @author Lukas Eder
 */
public final class CompileOptions {

    final List<? extends Processor> processors;

    public CompileOptions() {
        this(
            Collections.emptyList()
        );
    }

    private CompileOptions(List<? extends Processor> processors) {
        this.processors = processors;
    }

    @SuppressWarnings("hiding")
    public CompileOptions processors(Processor... processors) {
        return processors(Arrays.asList(processors));
    }

    @SuppressWarnings("hiding")
    public CompileOptions processors(List<? extends Processor> processors) {
        return new CompileOptions(processors);
    }
}

