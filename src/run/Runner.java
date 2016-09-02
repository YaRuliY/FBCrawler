package run;
import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.util.FileManager;
import graph.Librarian;
import model.User;
import unit.Crawler;
import unit.Queue;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;

public class Runner {
    public static void main(String[] args){
        /*Queue qu = new Queue();
        qu.addTask("https://www.facebook.com/lukyanenko.yaroslav");
        User u = qu.execute().get(0);
        System.out.println(u.toString());*/

        Model model = ModelFactory.createDefaultModel();
        InputStream in = FileManager.get().open("db.rdf");
        if (in == null) { throw new IllegalArgumentException("File not found"); }
        model.read(in, "");

        Queue qu = new Queue();
        qu.addTask("https://www.facebook.com/lukyanenko.yaroslav");
        User u = qu.execute().get(0);

        model.createResource();
        Librarian.accountToResource(model, u);

        FileWriter out = null;
        try {
            out = new FileWriter( "db.rdf" );
            model.write(out);
        }
        catch (IOException exc) {exc.printStackTrace();}

        finally {
            if (out != null) {
                try { out.close(); }
                catch (IOException e) { e.printStackTrace(); }
            }
        }
    }
}