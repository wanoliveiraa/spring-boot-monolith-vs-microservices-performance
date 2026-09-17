package io.github.wanoliveiraa.monolito.Infra;


import io.github.wanoliveiraa.monolito.Model.Cliente;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ClienteRepository extends JpaRepository<Cliente, Long> {


}
