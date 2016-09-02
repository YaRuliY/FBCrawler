package graph;

import graph.vocabularies.EFC;
import com.hp.hpl.jena.query.*;
import com.hp.hpl.jena.rdf.model.*;
import com.hp.hpl.jena.sparql.vocabulary.FOAF;
import model.User;

import java.util.ArrayList;

public class Librarian {

    static public Resource findResource(Model model, Property relation, String property) {

        Resource resource = null;
        String queryStr =
                "Select * WHERE { ?x " +
                        " <" + relation.getURI() + "> " +
                        "\"" + property + "\" } ";
        Query query = QueryFactory.create(queryStr);
        QueryExecution qexec = QueryExecutionFactory.create(query, model);
        try {
            ResultSet results = qexec.execSelect();
            QuerySolution soln = results.nextSolution();
            Resource x = soln.getResource("x");
            resource = x;
        } finally {
            qexec.close();
        }
        return resource;
    }

    static public Resource accountToResource (Model model, User facebook) {
        Resource resource;
        resource = model.createResource(facebook.ref);

        resource.addProperty(EFC.FCID, facebook.ref);
        resource.addProperty(FOAF.givenname, facebook.name);
        resource.addProperty(FOAF.family_name, facebook.secondName);
        //resource.addProperty(FOAF.gender, facebook.gender);
        if (facebook.birthday != null) resource.addProperty(FOAF.birthday, facebook.birthday.toString());
        if (facebook.birthPlace != null) resource.addProperty(EFC.birsdayplace, facebook.birthPlace);
        //resource.addProperty(EFC.school, facebook.school);
        if (facebook.currentWork != null) resource.addProperty(EFC.workIn, facebook.currentWork);
        if (facebook.location != null) resource.addProperty(EFC.liveIn, facebook.location);
        //resource.addProperty(FOAF.interest, facebook.interest);

        resource.addProperty(EFC.countOfSubscribers, Integer.toString(facebook.followersCount));
        resource.addProperty(EFC.countOfFriends, Integer.toString(facebook.friendsCount));
        /*resource.addProperty(EFC.countOfPhotos, Integer.toString(facebook.CountOfPhotos));
        resource.addProperty(EFC.countOfVideos, Integer.toString(facebook.CountOfVideos));
        resource.addProperty(EFC.countOfPosts, Integer.toString(facebook.CountOfPosts));
        resource.addProperty(EFC.countOfMusic, Integer.toString(facebook.CountOfMusic));
*/
        addProperty(model, facebook.friendsList, EFC.friend, resource);
        addProperty(model, facebook.education, EFC.school, resource);
        //addProperty(model, facebook.previousWorks, EFC.friend, resource);
        //addProperty(model, facebook.userLikes, EFC.friend, resource);

        return resource;
    }

    static void addProperty(Model model, ArrayList<String> arrayList, Property property , Resource resource){
        if(arrayList != null)
            for (int i = 0; i < arrayList.size(); i++) {
                if(model.getResource(arrayList.get(i)) != null) {
                    resource.addProperty((Property) property , model.getResource(arrayList.get(i)));
                }
            }
    }

    static public Resource addFriends(Model model, Resource person, ArrayList<String> friends) {
        String friendId;
        for (int i = 0; i < friends.size(); i++) {
            friendId = friends.get(i);
            createFriendship(model, friendId, person.getProperty(EFC.FCID).getString());
        }
        return person;
    }


    static public Model createFriendship(Model model, String URI1, String URI2) {
        Resource resource1 = model.getResource("http://Facebook/" + URI1);
        Resource resource2 = model.getResource("http://Facebook/" + URI2);
        resource1.addProperty(EFC.friend, model.createTypedLiteral(resource2, "resource"));
        resource2.addProperty(EFC.friend, model.createTypedLiteral(resource1, "resource"));
        return model;
    }

    static public ArrayList<String> friendList (Model model, String personStr) {
        ArrayList<String> friendList= new ArrayList<>();
        Resource person = model.getResource("http://Facebook/" + personStr);
        StmtIterator iterator = person.listProperties(EFC.friend);
        String buff;
        while (iterator.hasNext()) {
            friendList.add(iterator.next().getString());
        }

        return  friendList;
    }
}
