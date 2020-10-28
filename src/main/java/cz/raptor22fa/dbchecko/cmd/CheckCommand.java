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

import java.io.File;
import cz.raptor22fa.dbchecko.db.DbChecko;
import cz.raptor22fa.dbchecko.exception.MissingParameterDbCheckoException;
import picocli.CommandLine.Command;
import picocli.CommandLine.MissingParameterException;
import picocli.CommandLine.Model;
import picocli.CommandLine.Option;
import picocli.CommandLine.Spec;

/**
 * Checks if it is possible to connect to the database. It can be use to verify if the database is reachable from
 * the client.
 *
 * @author Roman Srom
 */
@Command(
        name = "check",
        mixinStandardHelpOptions = true,
        description = "Checks if it is possible to connect to the database."
)
public class CheckCommand implements Runnable {

    @Option(
            names = "-p", description = "path to properties file"
    )
    private File propertiesFile;

    @Option(
            names = "-c", description = "path to context file"
    )
    private File contextFile;

    @Spec
    private Model.CommandSpec commandSpec;

    @Override
    public void run() {
        DbChecko dbChecko;
        try {
            dbChecko = DbChecko.getInstance(propertiesFile, contextFile);
        } catch (MissingParameterDbCheckoException e) {
            throw new MissingParameterException(commandSpec.commandLine(),
                    commandSpec.args(), "Missing properties file or context file");
        }

        dbChecko.check();
    }
}
