/*
 * Copyright (c) 2014-2015. Vlad Ilyushchenko
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.nfsdb.examples.imports;

import com.nfsdb.Journal;
import com.nfsdb.exceptions.JournalException;
import com.nfsdb.exp.FileSink;
import com.nfsdb.exp.RecordSourcePrinter;
import com.nfsdb.factory.JournalFactory;

import java.io.File;
import java.io.IOException;

public class ExportCsvMain {
    public static void main(String[] args) throws JournalException, IOException {
        JournalFactory factory = new JournalFactory(args[0]);

        Journal r = factory.reader("trip_data_1.csv");
        try (FileSink sink = new FileSink(new File("d:/csv/1.csv"))) {
            RecordSourcePrinter printer = new RecordSourcePrinter(sink, ',');
            long t = System.currentTimeMillis();
            printer.print(r.rows());
            System.out.println(System.currentTimeMillis() - t);
        }
    }
}
