/*
 * MIT License
 *
 * Copyright (c) 2020 Roman Srom
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */
package cz.raptor22fa.dbchecko.cmd;

import org.apache.log4j.Logger;
import picocli.CommandLine;
import picocli.CommandLine.Command;

/**
 * DbChecko application.
 *
 * @author Roman Srom
 */
@Command(
        name = "dbchecko",
        mixinStandardHelpOptions = true,
        subcommands = {
                CheckCommand.class,
                SelectCommand.class,
                UpdateCommand.class
        },
        footer = {
                "Example Windows: java -cp target\\db-checko.jar;drivers\\* ",
                "\tcz.raptor22fa.dbchecko.cmd.DbCheckoApp check -p FILE",
                "Example Linux: java -cp target/db-checko.jar:drivers/* ",
                "\tcz.raptor22fa.dbchecko.cmd.DbCheckoApp check -p FILE"
        }
)
public class DbCheckoApp implements Runnable {

    private static final Logger LOG = Logger.getLogger(DbCheckoApp.class);

    public static void main(String... args) {
        CommandLine.run(new DbCheckoApp(), args);
    }

    @Override
    public void run() {
        CommandLine.usage(new DbCheckoApp(), System.err);
    }
}
