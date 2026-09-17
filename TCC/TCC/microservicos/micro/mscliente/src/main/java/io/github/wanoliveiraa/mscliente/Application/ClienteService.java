package io.github.wanoliveiraa.mscliente.Application;

import io.github.wanoliveiraa.mscliente.Infra.ClienteRepository;
import io.github.wanoliveiraa.mscliente.Model.Cliente;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ClienteService {

    private final ClienteRepository clienteRepository;

    public Cliente saveCliente(Cliente cliente){
        return clienteRepository.save(cliente);
    }

    @Transactional
    public Optional<Cliente> atualizarCliente(Cliente clienteAtualizados) {
        return clienteRepository.findById(clienteAtualizados.getId()).map(cliente -> {
            cliente.setNome(clienteAtualizados.getNome());
            cliente.setEmail(clienteAtualizados.getEmail());
            return clienteRepository.save(cliente);
        });
    }

    public Optional<Cliente> listaClientePorId(Long id) {
        return clienteRepository.findById(id);
    }
    public List<Cliente> listarTodosClientes() {
        return clienteRepository.findAll();
    }

}
