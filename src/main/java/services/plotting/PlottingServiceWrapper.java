package services.plotting;

import lombok.extern.slf4j.Slf4j;
import net.dv8tion.jda.api.entities.MessageChannel;

import java.io.IOException;

@Slf4j
public class PlottingServiceWrapper extends  PlottingService{
    private static final PlottingServiceWrapper instance = new PlottingServiceWrapper();
    public static PlottingServiceWrapper getInstance() {
        return instance;
    }

    public void inputdata(String[] content, MessageChannel channel) {
        if(content.length>1) {
            try {
                inputdata(1,content,channel);
            }
            catch (IOException e) {
                bot.sendErrorMessage("Unexpected Error occurred. Please Check the logs",channel);
                log.error("Unexpected Error occurred: ");
                e.printStackTrace();
            }
        }
        else
            bot.sendErrorMessage("Error: Please mind the syntax",channel);
        }
}
