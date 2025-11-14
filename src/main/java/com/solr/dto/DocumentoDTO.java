package com.solr.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

public class DocumentoDTO {
	
	@JsonProperty("id")
	private String id;
	
	@JsonProperty("titulo")
	private String titulo;
	
	@JsonProperty("cuerpo")
	private String cuerpo;
	
	public DocumentoDTO() {}
	
	public DocumentoDTO(String id, String cuerpo) {
		this.id = id;
		this.titulo = null;
		this.cuerpo = cuerpo;		
	}
	
	public DocumentoDTO(String id, String titulo, String cuerpo) {
		this.id = id;
		this.titulo = titulo;
		this.cuerpo = cuerpo;		
	}
	
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }
    
    public String getTitulo() {
    	return titulo;
    }
    
    public void setTitulo(String titulo) {
    	this.titulo = titulo;
    }
    
    public String getCuerpo() {
    	return cuerpo;
    }
    
    public void setCuerpo(String cuerpo) {
    	this.cuerpo = cuerpo;
    }

}
