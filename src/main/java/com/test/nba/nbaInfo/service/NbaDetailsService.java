package com.test.nba.nbaInfo.service;

import com.google.gson.Gson;
import com.opencsv.bean.CsvToBean;
import com.opencsv.bean.CsvToBeanBuilder;
import com.opencsv.bean.StatefulBeanToCsv;
import com.opencsv.bean.StatefulBeanToCsvBuilder;
import com.opencsv.exceptions.CsvDataTypeMismatchException;
import com.opencsv.exceptions.CsvRequiredFieldEmptyException;
import com.test.nba.nbaInfo.constants.Constants;
import com.test.nba.nbaInfo.domain.Player;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import redis.clients.jedis.Jedis;

import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class NbaDetailsService {

    @Autowired
    WSService webSocketService;

    @Autowired
    private RestTemplate restTemplate;

    @Autowired
    private Gson gson;

    private Jedis jedis;

    public NbaDetailsService() {
        jedis = new Jedis();
    }

    /*
            0 - at second :00,
            0/15 - every 15 minutes starting at minute :00,
            * - every hour,
            * - every day,
            * - every month,
            ? - any day of the week.
         */
    @Scheduled(cron = "0 0/15 * * * ?")
    public List<Player> getPlayerDetails() throws IOException {

        String fileName = (!new File(Constants.PREFIX_FILE + Constants.CSV_RESPONSE_FILE).isFile()) ? "players.csv" : "players_response.csv";
        List<Player> players = readFromPlayersFile(Constants.PREFIX_FILE + fileName);
        writeToCSVResultFile(players, Constants.PREFIX_FILE + Constants.CSV_RESPONSE_FILE);
        return players;
    }

    public List<Player> readFromPlayersFile(String filePath) throws FileNotFoundException {

        Reader reader = new BufferedReader(new FileReader(filePath));

        CsvToBean<Player> csvToBean = new CsvToBeanBuilder(reader)
                .withType(Player.class)
                .withIgnoreLeadingWhiteSpace(true)
                .build();

        List<Player> players = csvToBean.parse();
        /*
        in some of the cases while handling debug mode ,
        a response file can be created but with no records , due the end of debug session.
        this will start creating the file from the input again (scenario not possible
        with real life DB.)
        */
        if(players.isEmpty()){
           return readFromPlayersFile(Constants.PREFIX_FILE + Constants.CSV_INPUT_FILE);
        }

        List<Player> newDetails = new ArrayList<>();
        for (Player player: players) {
            String idStr = String.valueOf(player.getId());
            Player playerFromApi = callPlayersAPIById(Constants.URL_PLAYER_BY_ID,idStr);//checkIfExistInCache(player, idStr);
            playerFromApi.setNickname(player.getNickname()); //to avoid override of nickName - not exist on API side.
            newDetails.add(playerFromApi);
        }
        System.out.println("players:" + newDetails);
        return newDetails;
    }

  //  @CachePut(cacheNames = "nbaPlayers", key = "id", unless="result.id != null ")
    private void updateCacheIfWriteToFile(StatefulBeanToCsv beanToCsv, Player player, String id) throws Exception {
        System.out.println("going to write to file");
        beanToCsv.write(player);
        jedis.set(id,gson.toJson(player));
        webSocketService.notifyClient(Constants.NOTIFICATION_PLAYERS_DETAILS_WAS_CHANGED_IN_CSV_FILE);
    }

    /*I've tried to use annotation of @Cacheable and @CachePut
      but it didn't work for me. so I used Jedis client insted
      in order to get and set values from cache.
     */
    //@Cacheable(cacheNames = "nbaPlayers", key = "#player.id")
    private void writeToCSVResultFile(List<Player> players, String fileName){
        // List<MyBean> beans comes from somewhere earlier in your code.
        Writer writer = null;
        try {
            writer = new FileWriter(fileName);
            StatefulBeanToCsv beanToCsv = new StatefulBeanToCsvBuilder(writer).build();
            players.forEach(player ->
            {
                long playerId = player.getId();
                String idStr = String.valueOf(playerId);
                try {
                    if(!jedis.exists(idStr)){
                        updateCacheIfWriteToFile(beanToCsv,player,idStr);
                       // jedis.set(idStr,gson.toJson(player));
                    }else{
                        System.out.println("values already exist in cache");
                        beanToCsv.write(player);
                    }
                } catch (CsvDataTypeMismatchException | CsvRequiredFieldEmptyException e) {
                    e.printStackTrace();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            });
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public Player callPlayersAPIById(String url,String parameter){
        Map<String, String> params = new HashMap<String, String>();
        params.put("id", parameter);

        Player player = restTemplate.getForObject(url, Player.class, params);
        System.out.println(player);
        return player;
    }
}
