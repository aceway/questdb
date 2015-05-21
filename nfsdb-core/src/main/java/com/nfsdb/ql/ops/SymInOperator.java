/*
 * Copyright (c) 2014. Vlad Ilyushchenko
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

package com.nfsdb.ql.ops;

import com.nfsdb.collections.IntHashSet;
import com.nfsdb.collections.ObjHashSet;
import com.nfsdb.ql.Record;
import com.nfsdb.ql.SymFacade;
import com.nfsdb.ql.parser.ParserException;
import com.nfsdb.storage.ColumnType;
import com.nfsdb.storage.SymbolTable;

public class SymInOperator extends AbstractVirtualColumn implements Function {

    private final IntHashSet set = new IntHashSet();
    private final ObjHashSet<CharSequence> values = new ObjHashSet<>();
    private VirtualColumn lhs;

    public SymInOperator() {
        super(ColumnType.BOOLEAN);
    }

    @Override
    public boolean getBool(Record rec) {
        return set.contains(lhs.getInt(rec));
    }

    @Override
    public boolean isConstant() {
        return lhs.isConstant();
    }

    @Override
    public void prepare(SymFacade facade) {
        lhs.prepare(facade);
        SymbolTable tab = lhs.getSymbolTable();
        for (int i = 0, n = values.size(); i < n; i++) {
            int k = tab.getQuick(values.get(i));
            if (k > -1) {
                set.add(k);
            }
        }
    }

    @Override
    public void setArg(int pos, VirtualColumn arg) throws ParserException {
        if (pos == 0) {
            lhs = arg;
        } else {
            values.add(arg.getStr(null).toString());
        }
    }

    @Override
    public void setArgCount(int count) {
    }
}