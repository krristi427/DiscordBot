package services.authorisation;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import dataObjects.AuthorisationPair;
import lombok.Getter;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Role;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

//TODO make Constructor
public class AuthorisationService {
    public static final String FILE_NAME = "src/main/resources/json/authorisationProperties.json";
    public static final Path FILE_NAME_PATH = Paths.get(FILE_NAME);

    private static AuthorisationService instance;
    public static AuthorisationService getInstance() throws IOException {
        if (instance==null)
                instance = new AuthorisationService();
        return instance;
    }

    private final Gson gson = new Gson();
    private @Getter List<AuthorisationPair> authorisationPairs;

    AuthorisationService() throws IOException {
        authorisationPairs = gson.fromJson(Files.readString(FILE_NAME_PATH), new TypeToken<ArrayList<AuthorisationPair>>(){}.getType());
    }

    public boolean isAuthorised(String authorisationRequestName, Member member)
    {
        List<Role> roleList = member.getRoles();
        String[] comparison = new String[0];
        for (AuthorisationPair p: authorisationPairs)
        {
            if(p.name.compareTo(authorisationRequestName)==0)
            {
                comparison=p.members;
            }
        }

        for (Role role : roleList)
        {
            for (String memberName : comparison)
            {
                if(memberName.equalsIgnoreCase(role.getName()))
                    return true;
            }
        }
        return false;
    }

    public boolean isAuthorised(int authorisationRequestID, Member member)
    {
        List<Role> roleList = member.getRoles();
        String[] comparison = authorisationPairs.get(authorisationRequestID).members;

        for (Role role : roleList)
        {
            for (String memberName : comparison)
            {
                if(memberName.equalsIgnoreCase(role.getName()))
                    return true;
            }
        }
        return false;
    }
}
