package com.example.ms_usuarios.service;

import com.example.ms_usuarios.dto.LoginRequest;
import com.example.ms_usuarios.dto.UsuarioRequest;
import com.example.ms_usuarios.exception.ApiException;
import com.example.ms_usuarios.model.Usuario;
import com.example.ms_usuarios.repository.UsuarioRepository;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;

// Lógica de usuarios y login
@Service
public class UsuarioService {

    private final UsuarioRepository repositorio;
    private final PasswordEncoder encoder;

    public UsuarioService(UsuarioRepository repositorio, PasswordEncoder encoder) {
        this.repositorio = repositorio;
        this.encoder = encoder;
    }

    // Devuelve el usuario si las credenciales son correctas; si no, error 401
    public Usuario login(LoginRequest request) {
        Usuario usuario = repositorio.findByCorreo(request.correo().trim())
                .orElseThrow(this::credencialesIncorrectas);
        if (!encoder.matches(request.clave(), usuario.getClave())) {
            throw credencialesIncorrectas();
        }
        return usuario;
    }

    public List<Usuario> listar() {
        return repositorio.findAll();
    }

    public Usuario obtener(Integer id) {
        return repositorio.findById(id)
                .orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "El usuario no existe"));
    }

    public Usuario crear(UsuarioRequest request) {
        String correo = request.correo().trim();
        if (repositorio.existsByCorreo(correo)) {
            throw new ApiException(HttpStatus.CONFLICT, "El correo ya está en uso");
        }
        Usuario usuario = new Usuario();
        copiarDatos(usuario, request, correo);
        return repositorio.save(usuario);
    }

    public Usuario actualizar(Integer id, UsuarioRequest request) {
        Usuario usuario = obtener(id);
        String correo = request.correo().trim();
        // El correo no puede pertenecer a otro usuario
        boolean usadoPorOtro = repositorio.findByCorreo(correo)
                .filter(otro -> !otro.getId().equals(id))
                .isPresent();
        if (usadoPorOtro) {
            throw new ApiException(HttpStatus.CONFLICT, "El correo ya está en uso");
        }
        copiarDatos(usuario, request, correo);
        return repositorio.save(usuario);
    }

    public void eliminar(Integer id) {
        if (!repositorio.existsById(id)) {
            throw new ApiException(HttpStatus.NOT_FOUND, "El usuario no existe");
        }
        repositorio.deleteById(id);
    }

    private void copiarDatos(Usuario usuario, UsuarioRequest request, String correo) {
        usuario.setNombre(request.nombre().trim());
        usuario.setCorreo(correo);
        usuario.setClave(encoder.encode(request.clave()));
        usuario.setPerfil(request.perfil());
    }

    // No se indica si falló el correo o la clave
    private ApiException credencialesIncorrectas() {
        return new ApiException(HttpStatus.UNAUTHORIZED, "Correo o contraseña incorrectos");
    }
}