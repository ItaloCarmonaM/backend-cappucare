package com.example.ms_usuarios.config;

import com.example.ms_usuarios.model.Perfil;
import com.example.ms_usuarios.model.Usuario;
import com.example.ms_usuarios.repository.UsuarioRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
public class DatosIniciales {

    @Bean
    CommandLineRunner cargarUsuarios(UsuarioRepository repositorio, PasswordEncoder encoder) {
        return args -> {
            // Solo se cargan si la tabla está vacía
            if (repositorio.count() > 0) return;

            repositorio.save(crearUsuario("María González", "encargado@cappucare.cl", "cappu123", Perfil.ENCARGADO, encoder));
            repositorio.save(crearUsuario("Pedro Soto", "consultor@cappucare.cl", "cappu123", Perfil.CONSULTOR, encoder));
        };
    }

    private Usuario crearUsuario(String nombre, String correo, String clave, Perfil perfil, PasswordEncoder encoder) {
        Usuario usuario = new Usuario();
        usuario.setNombre(nombre);
        usuario.setCorreo(correo);
        usuario.setClave(encoder.encode(clave));
        usuario.setPerfil(perfil);
        return usuario;
    }
}