package scalable.com.music.commands;

import com.arangodb.entity.BaseDocument;
import org.json.JSONArray;
import org.json.JSONObject;
import scalable.com.exceptions.ValidationException;
import scalable.com.shared.classes.Arango;
import scalable.com.shared.classes.CommandVerifier;
import scalable.com.shared.classes.Responder;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Map;
import java.util.Set;

public class FavoriteAlbumsOfOthersCommand extends CommandVerifier {
    String user_id;
    @Override
    public String getCommandName() {
        return "FavoriteAlbumsOfOthers";
    }

    @Override
    public String execute() {
        Arango arango = null;
        try {
            String user_id = this.uriParams.getString("user_id");
            //String w = this.uriParams.getString("user_id");

            arango = Arango.getInstance();
            arango.createCollectionIfNotExists("spotifyArangoDb","FavoriteAlbums",false);
            var a = arango.filterCollection("spotifyArangoDb", "FavoriteAlbums", "user_id", user_id);
            JSONObject al = new JSONObject();
            ArrayList<BaseDocument> docs = new ArrayList<>();
            Object[] albums = a.stream().toArray();
            JSONArray toCombine = new JSONArray();
            for(int i = 0; i < albums.length; i++){
                BaseDocument th = (BaseDocument) albums[i];
                Map fordoc = th.getProperties();
                JSONObject f = new JSONObject(fordoc);
                toCombine.put(f);
            }
            JSONObject combined = new JSONObject();
            for(int i = 0; i < toCombine.length(); i++){
                JSONObject current = toCombine.getJSONObject(i);
                Set<String> keys = current.keySet();
                for(String s : keys){
                    if(!s.equals("user_id"))
                        combined.append(s, current.get(s));

                }
            }

            return Responder.makeDataResponse(combined);
        } catch (Exception e) {
            return Responder.makeErrorResponse(e.getMessage(), 404);
        }
    }

    @Override
    public String getRestAPIMethod() {
        return "GET";
    }

    @Override
    public boolean isAuthNeeded() {
        return true;
    }

    @Override
    public void validateAttributeTypes() throws ValidationException {
//        try{
//            this.user_id= body.getString("user_id");
//        }     catch(Exception e){
//            e.printStackTrace();
//        }

    }
}
