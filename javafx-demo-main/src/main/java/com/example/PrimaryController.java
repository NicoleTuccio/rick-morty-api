package com.example;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.control.Pagination;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.VBox;



public class PrimaryController implements Initializable {

    @FXML Pagination pagination;

    private int pagina = 1;

    public FlowPane carregar(){
        try {
            var url = new URL("https://rickandmortyapi.com/api/character?page=" + pagina); //colocar dentro do trycath para não dar erro
            var conexao = url.openConnection();//abrir conexao com o site
            conexao.connect();//conectar com o site
            var is = conexao.getInputStream();//tunel onde as informações vão chegando
            var reader = new BufferedReader(new InputStreamReader(is));//usado para ler dados de entrada 
            var json = reader.readLine();//ler linha

            var lista = jsonParaLista(json);//converter json para uma lista de personagem
            return mostrarPersonagens(lista);//metodo
            
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    private FlowPane mostrarPersonagens(List<Personagem> lista) {
        var flow = new FlowPane();
        flow.setHgap(20); //padding
        flow.setVgap(20);
        lista.forEach(p -> {
            var image = new ImageView(new Image(p.getImage()));
            image.setFitHeight(100); //tamanho da imagem
            image.setFitWidth(100);
            var labelName = new Label(p.getName());
            var labelSpecies = new Label(p.getSpecies());
            flow.getChildren().add(new VBox(image, labelName, labelSpecies));//criar um rotulo e colocar na tela
            
        });
        return flow;
    }
    private List<Personagem> jsonParaLista(String json) throws JsonMappingException, JsonProcessingException {
        var mapper = new ObjectMapper();//é a classe que faz a conversão de json para objeto
        var results = mapper.readTree(json).get("results");//ler a arvore(json) extrai apenas o results de dentro da api
        List<Personagem> lista = new ArrayList<>();

        results.forEach(personagem -> {
            try {
                var p = mapper.readValue(personagem.toString(), Personagem.class);//converte o personagem para o java
                lista.add(p);//adiciona na lista
            } catch (JsonProcessingException e) {
                e.printStackTrace();
            } 
            //System.out.println(personagem.get("name")); //pegar o campo nome do results
        });
        return lista;
    
    }

    @Override
    public void initialize(URL arg0, ResourceBundle arg1) {
        pagination.setPageFactory(pag -> { 
            pagina = pag + 1;
            return carregar();
        });
    }
    
}
