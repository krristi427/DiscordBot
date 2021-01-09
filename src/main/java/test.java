import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

public class test {
    public static void main(String[] args) {
        try {
            String line;
            Process p = Runtime.getRuntime().exec("python src/main/python/plotter.py raw");
            BufferedReader input = new BufferedReader(new InputStreamReader(p.getInputStream()));
            while ((line = input.readLine()) != null)
            {
                System.out.println("In: "+line);
            }
            input.close();

            String errorline;
            BufferedReader errorinput = new BufferedReader(new InputStreamReader(p.getErrorStream()));
            while ((errorline = errorinput.readLine()) != null)
            {
                System.out.println(errorline);
            }
            errorinput.close();

        } catch (IOException e) {
            e.printStackTrace();
            System.out.println("STOP");
        }
    }

}
