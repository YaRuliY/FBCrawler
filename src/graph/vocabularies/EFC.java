package graph.vocabularies;

import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import com.hp.hpl.jena.rdf.model.Property;
import com.hp.hpl.jena.rdf.model.Resource;

public class EFC {
    private static Model m_model = ModelFactory.createDefaultModel();
    public static final String NS = "http://xmlns.com/EFC/";
    public static final Property FCID;
    public static final Resource NAMESPACE;
    public static final Property friend;
    public static final Resource collegue;
    public static final Resource emplouee;
    public static final Resource boss;

    public static final Property birsdayplace;
    public static final Property school;
    public static final Property workIn;
    public static final Property liveIn;

    public static final Property pageCreationDate;
    public static final Property countOfFriends;
    public static final Property countOfPosts;
    public static final Property countOfVideos;
    public static final Property countOfMusic;
    public static final Property countOfPhotos;
    public static final Property countOfSubscribers;
    public static final Property WT;

    public EFC() {
    }

    public static String getURI() {
        return "http://xmlns.com/EFC/";
    }

    static {
        NAMESPACE = m_model.createResource("http://xmlns.com/EFC/");
        friend = m_model.createProperty("http://xmlns.com/EFC/friend");
        collegue = m_model.createResource("http://xmlns.com/EFC/collegue");
        emplouee = m_model.createResource("http://xmlns.com/EFC/emplouee");
        boss = m_model.createResource("http://xmlns.com/EFC/boss");

        liveIn = m_model.createProperty("http://xmlns.com/EFC/liveIn");
        birsdayplace = m_model.createProperty("http://xmlns.com/EFC/birsdayplace");
        school = m_model.createProperty("http://xmlns.com/EFC/school");
        workIn = m_model.createProperty("http://xmlns.com/EFC/workIn");

        pageCreationDate = m_model.createProperty("http://xmlns.com/EFC/pageCreationDate");
        countOfFriends = m_model.createProperty("http://xmlns.com/EFC/countOfFriends");
        countOfPosts = m_model.createProperty("http://xmlns.com/EFC/countOfPosts");
        countOfVideos = m_model.createProperty("http://xmlns.com/EFC/countOfVideos");
        countOfMusic = m_model.createProperty("http://xmlns.com/EFC/countOfMusic");
        countOfPhotos = m_model.createProperty("http://xmlns.com/EFC/countOfPhotos");
        countOfSubscribers = m_model.createProperty("http://xmlns.com/EFC/Subscribers");
        FCID = m_model.createProperty("http://xmlns.com/EFC/FCID");
        WT = m_model.createProperty("http://xmlns.com/EFC/WT");
    }
}
