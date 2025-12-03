package com.solr.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

public class DocumentoDTO {
	
	@JsonProperty("idDoc")
	private long idDoc;
	
	@JsonProperty("titulo")
	private String titulo;
	
	@JsonProperty("cuerpo")
	private String cuerpo;
	
	public DocumentoDTO() {}
	
	public DocumentoDTO(long id, String cuerpo) {
		this.idDoc = id;
		this.titulo = null;
		this.cuerpo = cuerpo;		
	}
	
	public DocumentoDTO(long id, String titulo, String cuerpo) {
		this.idDoc = id;
		this.titulo = titulo;
		this.cuerpo = cuerpo;		
	}
	
    public long getId() {
        return idDoc;
    }

    public void setId(long id) {
        this.idDoc = id;
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
