package io.github.wanoliveiraa.mscliente.Infra;

import io.github.wanoliveiraa.mscliente.Model.Cliente;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ClienteRepository extends JpaRepository<Cliente, Long> {
}
