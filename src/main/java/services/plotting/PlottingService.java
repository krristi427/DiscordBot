package services.plotting;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.MessageChannel;
import org.jetbrains.annotations.NotNull;
import services.Service;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.*;


public abstract class PlottingService extends Service {


    public class PythonError extends Exception{
        public String errorLines;
        PythonError(String errorLines)
        {
            this.errorLines=errorLines;
        };
    }

    public void inputdata(int startindex, @NotNull String [] content, @NotNull MessageChannel channel) throws IOException {
        FileWriter writer = new FileWriter("src/main/resources/misc/data.txt");
        for (int i=startindex;i<content.length;i++)
        {
            String[] s = content[i].split(":");
            writer.write(s[0]+" "+s[1]+"\n");
        }

        writer.close();
        channel.sendMessage("Saved data").queue();
    }

    public void plot(@NotNull String[] content, @NotNull MessageChannel channel) throws IOException, PythonError {
        Process p = Runtime.getRuntime().exec("python src/main/python/plotter.py " + content[1]);
        BufferedReader errorinput = new BufferedReader(new InputStreamReader(p.getErrorStream()));
        String errorline;
        String error="";
        while ((errorline = errorinput.readLine()) != null) {
            error+="Python Error occurred: " + errorline+"\n";
            channel.sendMessage("Python Error occurred: " + errorline).queue();//Test purpose
        }
        errorinput.close();
        if(error!="")
        {
            throw new PythonError(error);
        }

        //send the Diagram
        if(content[1].compareTo("raw")==0)
        {
            BufferedReader input = new BufferedReader(new InputStreamReader(p.getInputStream()));
            String line;
            String buff="";
            while ((line = input.readLine()) != null) {
                buff+=line+"\n";
            }
            input.close();
            EmbedBuilder info = new EmbedBuilder();
            info.setTitle("Plot:");
            info.setDescription(buff);
            channel.sendMessage(info.build()).queue();
        }
        else {
            File file = new File("src/main/resources/misc/dataoutput.png");
            channel.sendFile(file).queue();
        }
    }

    public void plotugly(@NotNull MessageChannel channel) throws IOException { //only for Test purpose //TODO Delete this funktion
        int width = 250;
        int height = 250;

        // Constructs a BufferedImage of one of the predefined image types.
        BufferedImage bufferedImage = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);

        // Create a graphics which can be used to draw into the buffered image
        Graphics2D g2d = bufferedImage.createGraphics();

        // fill all the image with white
        g2d.setColor(Color.white);
        g2d.fillRect(0, 0, width, height);

        // create a circle with black
        g2d.setColor(Color.black);
        g2d.fillOval(0, 0, width, height);

        // create a string with yellow
        g2d.setColor(Color.yellow);
        g2d.drawString("this is a diagram", 50, 120);

        // Disposes of this graphics context and releases any system resources that it is using.
        g2d.dispose();

        // Save as PNG
        File file = new File("src/main/resources/misc/dataoutput.png");


        ImageIO.write(bufferedImage, "png", file);   //could throw error here

        //send the Diagram
        channel.sendFile(file).queue();                         //could throw error here
    }
}
