package com.pet.buscaativa.config;

import com.pet.buscaativa.entities.Usuario;
import com.pet.buscaativa.entities.enums.TipoUsuario;
import com.pet.buscaativa.repositories.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

import java.util.Arrays;

@Configuration
@Profile("dev")
public class DevConfig implements CommandLineRunner {

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Override
    public void run(String... args) throws Exception {

        Usuario user1 = new Usuario(null, "user1@gmail.com", "user111", TipoUsuario.ADMINISTRADOR);
        Usuario user2 = new Usuario(null, "user2@gmail.com", "user222", TipoUsuario.MEDICO);
        Usuario user3 = new Usuario(null, "user3@gmail.com", "user333", TipoUsuario.RECEPCAO);

        usuarioRepository.saveAll(Arrays.asList(user1, user2, user3));
    }
}
