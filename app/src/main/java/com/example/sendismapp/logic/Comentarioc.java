package com.example.sendismapp.logic;

public class Comentarioc {
    private String comentario;
    private String fecha;
    private String imagen;
    private String creador;
    private String propietario;
    private String ruta;
    private String nickname;

    public Comentarioc(String comentario, String fecha, String imagen, String creador, String propietario, String ruta){
        this.comentario = comentario;
        this.fecha = fecha;
        this.imagen = imagen;
        this.creador = creador;
        this.propietario = propietario;
        this.ruta = ruta;
    }
    public Comentarioc ()
    {

    }

    public String getComentario() {
        return comentario;
    }

    public void setComentario(String comentario) {
        this.comentario = comentario;
    }

    public String getFecha() {
        return fecha;
    }

    public void setFecha(String fecha) {
        this.fecha = fecha;
    }

    public String getImagen() {
        return imagen;
    }

    public void setImagen(String imagen) {
        this.imagen = imagen;
    }

    public String getCreador() {
        return creador;
    }

    public void setCreador(String creador) {
        this.creador = creador;
    }

    public String getPropietario() {
        return propietario;
    }

    public void setPropietario(String propietario) {
        this.propietario = propietario;
    }

    public String getRuta() {
        return ruta;
    }

    public void setRuta(String ruta) {
        this.ruta = ruta;
    }

    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public String toString()
    {
        return  "Usuario: " + this.nickname + '\n' +
                "Ruta: " + this.ruta + '\n'+
                "Fecha:" + this.fecha;
    }
}
