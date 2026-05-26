package com.soulup.service;

import com.soulup.dto.UsuarioRequest;
import com.soulup.dto.UsuarioResponse;
import com.soulup.entity.Usuario;
import com.soulup.exception.BusinessException;
import com.soulup.exception.ResourceNotFoundException;
import com.soulup.repository.UsuarioRepository;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class UsuarioService {

    private final UsuarioRepository usuarioRepository;

    public UsuarioService(UsuarioRepository usuarioRepository) {
        this.usuarioRepository = usuarioRepository;
    }

    @Transactional
    public UsuarioResponse criar(UsuarioRequest request) {
        if (usuarioRepository.existsByEmail(request.email())) {
            throw new BusinessException("Ja existe um usuario cadastrado com este email.");
        }

        Usuario usuario = new Usuario(request.nome(), request.email(), request.senha());
        return UsuarioResponse.fromEntity(usuarioRepository.save(usuario));
    }

    @Transactional(readOnly = true)
    public List<UsuarioResponse> listar() {
        return usuarioRepository.findAll()
                .stream()
                .map(UsuarioResponse::fromEntity)
                .toList();
    }

    @Transactional(readOnly = true)
    public UsuarioResponse buscarPorId(Long id) {
        Usuario usuario = buscarEntidadePorId(id);
        return UsuarioResponse.fromEntity(usuario);
    }

    @Transactional(readOnly = true)
    public Usuario buscarEntidadePorId(Long id) {
        return usuarioRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Usuario nao encontrado com id " + id + "."));
    }
}
