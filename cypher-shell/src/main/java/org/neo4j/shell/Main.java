package org.neo4j.shell;

import org.neo4j.shell.cli.CliArgHelper;
import org.neo4j.shell.cli.CliArgs;
import org.neo4j.shell.commands.CommandHelper;
import org.neo4j.shell.log.AnsiLogger;
import org.neo4j.shell.log.Logger;

import javax.annotation.Nonnull;

import static org.neo4j.shell.exception.Helper.getFormattedMessage;

public class Main {

    public static void main(String[] args) {
        CliArgs cliArgs = CliArgHelper.parse(args);

        Main main = new Main();
        main.startShell(cliArgs);
    }

    void startShell(@Nonnull CliArgs cliArgs) {
        ConnectionConfig connectionConfig = new ConnectionConfig(cliArgs.getHost(),
                cliArgs.getPort(),
                cliArgs.getUsername(),
                cliArgs.getPassword());

        Logger logger = new AnsiLogger();
        try {
            CypherShell shell = new CypherShell(logger, cliArgs.getFormat());

            ShellRunner shellRunner = ShellRunner.getShellRunner(cliArgs, shell, logger);

            CommandHelper commandHelper = new CommandHelper(logger, shellRunner.getHistorian(), shell);

            shell.setCommandHelper(commandHelper);
            shell.connect(connectionConfig);

            int code = shellRunner.runUntilEnd();
            System.exit(code);
        } catch (Throwable e) {
            logger.printError(getFormattedMessage(e));
            System.exit(1);
        }
    }

}
