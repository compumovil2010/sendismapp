package com.example.sendismapp.logic;

public class Usuario {

    private String correo;
    private String nombre;
    private String contrasena;
    private String experiencia;
    private String nacionalidad;
    private String imagen;
    private String nickname;
    private float peso;
    private float altura;
    private int edad;

    public Usuario(String correo, String nombre, String contrasena, String experiencia, String imagen, float peso, float altura, int edad, String nacionalidad, String nickname) {
        this.correo = correo;
        this.nombre = nombre;
        this.contrasena = contrasena;
        this.experiencia = experiencia;
        this.imagen = imagen;
        this.peso = peso;
        this.altura = altura;
        this.edad = edad;
        this.nacionalidad = nacionalidad;
        this.nickname = nickname;
    }

    public String getCorreo() {
        return correo;
    }

    public void setCorreo(String correo) {
        this.correo = correo;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getContrasena() {
        return contrasena;
    }

    public void setContrasena(String contrasena) {
        this.contrasena = contrasena;
    }

    public String getExperiencia() {
        return experiencia;
    }

    public void setExperiencia(String experiencia) {
        this.experiencia = experiencia;
    }

    public String getImagen() {
        return imagen;
    }

    public void setImagen(String imagen) {
        this.imagen = imagen;
    }

    public float getPeso() {
        return peso;
    }

    public void setPeso(float peso) {
        this.peso = peso;
    }

    public float getAltura() {
        return altura;
    }

    public void setAltura(float altura) {
        this.altura = altura;
    }

    public int getEdad() {
        return edad;
    }

    public void setEdad(int edad) {
        this.edad = edad;
    }

    public String getNacionalidad(){
        return nacionalidad;
    }

    public void setNacionalidad(String nacionalidad){
        this.nacionalidad = nacionalidad;
    }

    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }
}
