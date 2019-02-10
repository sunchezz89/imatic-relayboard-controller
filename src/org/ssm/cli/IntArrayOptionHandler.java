package org.ssm.cli;

import org.kohsuke.args4j.CmdLineException;
import org.kohsuke.args4j.CmdLineParser;
import org.kohsuke.args4j.OptionDef;
import org.kohsuke.args4j.spi.OptionHandler;
import org.kohsuke.args4j.spi.Parameters;
import org.kohsuke.args4j.spi.Setter;

public class IntArrayOptionHandler extends OptionHandler<Integer> {


    public IntArrayOptionHandler(CmdLineParser parser, OptionDef option, Setter<? super Integer> setter) {
        super(parser, option, setter);
    }

    @Override
    public int parseArguments(Parameters params) throws CmdLineException {
        int counter = 0;
        for (; counter < params.size(); counter++) {
            String param = params.getParameter(counter);

            if (param.startsWith("-")) {
                break;
            }

            for (String p : param.split(",")) {
                setter.addValue((Integer.parseInt(p) - 1));
            }
        }

        return counter;
    }

    @Override
    public String getDefaultMetaVariable() {
        return "1,3,5";
    }
}
