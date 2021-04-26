package com.example.services;

import com.example.PersistenceManager;
import com.example.models.Competitor;
import com.example.models.CompetitorDTO;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.annotation.PostConstruct;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import org.codehaus.jettison.json.JSONException;
import org.codehaus.jettison.json.JSONObject;

/**
 *
 * @author Mauricio
 */
@Path("/competitors")
@Produces(MediaType.APPLICATION_JSON)
public class CompetitorService {

    @PersistenceContext(unitName = "CompetitorsPU")
    EntityManager entityManager;

    @GET
    @Path("/get")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getAll() {
        Query q = entityManager.createQuery("select u from Competitor u order by u.surname ASC");
        List<Competitor> competitors = q.getResultList();
        return Response.status(200).header("Access-Control-Allow-Origin", "*").entity(competitors).build();
    }

    @POST
    @Path("/add")
    @Produces(MediaType.APPLICATION_JSON)
    public Response createCompetitor(CompetitorDTO competitor) {
        JSONObject rta = new JSONObject();

        Competitor competitorTmp = new Competitor();
        competitorTmp.setAddress(competitor.getAddress());
        competitorTmp.setAge(competitor.getAge());
        competitorTmp.setCellphone(competitor.getCellphone());
        competitorTmp.setCity(competitor.getCity());
        competitorTmp.setCountry(competitor.getCountry());
        competitorTmp.setName(competitor.getName());
        competitorTmp.setSurname(competitor.getSurname());
        competitorTmp.setTelephone(competitor.getTelephone());
        competitorTmp.setPassword(competitor.getPassword());

        try {
            entityManager.getTransaction().begin();
            entityManager.persist(competitorTmp);
            entityManager.getTransaction().commit();
            entityManager.refresh(competitorTmp);
            rta.put("competitor_id", competitorTmp.getId());
        } catch (Throwable t) {
            t.printStackTrace();
            if (entityManager.getTransaction().isActive()) {
                entityManager.getTransaction().rollback();
            }
            competitorTmp = null;
        } finally {
            entityManager.clear();
            entityManager.close();
        }
        return Response.status(200).header("Access-Control-Allow-Origin", "*").entity(rta).build();
    }

    @POST
    @Path("/login")
    @Produces(MediaType.APPLICATION_JSON)
    public Response loginCompetitor(CompetitorDTO competitor) {
        System.out.println("select u from Competitor u WHERE u.address = '" + competitor.getAddress() + "' AND  u.password = '" + competitor.getPassword() + "'");
        Query q = entityManager.createQuery("select u from Competitor u WHERE u.address = '" + competitor.getAddress() + "' AND  u.password = '" + competitor.getPassword() + "'");
        List<Competitor> competitors = q.getResultList();

        if (competitors.size() > 0) {
            return Response.status(200).header("Access-Control-Allow-Origin", "*").entity(competitors).build();
        }
        JSONObject rta = new JSONObject();
        try {
            rta.put("message", "Inicio de sesión incorrecto");
        } catch (JSONException ex) {
            Logger.getLogger(CompetitorService.class.getName()).log(Level.SEVERE, null, ex);
        }
        return Response.status(406).header("Access-Control-Allow-Origin", "*").entity(rta).build();
    }

    @PostConstruct
    public void init() {
        try {
            entityManager
                    = PersistenceManager.getInstance().getEntityManagerFactory().createEntityManager();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
