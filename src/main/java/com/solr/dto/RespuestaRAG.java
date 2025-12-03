package com.solr.dto;

import java.util.List;

public class RespuestaRAG {
    
    private String resumen;
    private List<DocumentoDTO> resultados;
    
    public RespuestaRAG(String resumen, List<DocumentoDTO> resultados) {
        this.resumen = resumen;
        this.resultados = resultados;
    }

    public String getResumen() {
        return resumen;
    }

    public void setResumen(String resumen) {
        this.resumen = resumen;
    }

    public List<DocumentoDTO> getResultados() {
        return resultados;
    }

    public void setResultados(List<DocumentoDTO> resultados) {
        this.resultados = resultados;
    }
}
