import http from 'k6/http';
import { check, sleep, group } from 'k6';

export const options = {
  vus: 1000, // 1000 usuários simultâneos
  duration: '1m',
  thresholds: {
    'http_req_failed{recurso:pedidos}': ['rate<0.01'],      // <1% falha
    'http_req_duration{recurso:pedidos}': ['p(95)<750'],    // 95% < 750
    'checks{recurso:pedidos}': ['rate>0.99'],               // 99% dos checks válidos
  },
  tags: { teste: 'cenario1' }, // tag global
};

const BASE_URL = 'http://10.255.255.254:8080';
const PEDIDO_API_PATH = '/api/pedidos';

const TOTAL_CLIENTES = 1000;
const TOTAL_PRODUTOS = 2000;

function randomInt(min, max) {
  return Math.floor(Math.random() * (max - min + 1)) + min;
}

function generatePedidoPayload() {
  const clienteId = randomInt(1, TOTAL_CLIENTES);
  const quantidadeItens = randomInt(1, 3);
  const itens = [];

  for (let i = 0; i < quantidadeItens; i++) {
    const produtoId = randomInt(1, TOTAL_PRODUTOS);
    //itens.push({ produto: { id: produtoId } });
    itens.push({ produtoId: produtoId });
  }

  // return JSON.stringify({
  //   cliente: { id: clienteId },
  //   itens: itens,
  // });
  return JSON.stringify({
    clienteId: clienteId,
    itens: itens,
  });
}

export default function () {
  const headers = { 'Content-Type': 'application/json' };

  group('Criar pedido', () => {
    const payload = generatePedidoPayload();

    const res = http.post(`${BASE_URL}${PEDIDO_API_PATH}`, payload, {
      headers,
      tags: {
        tipo: 'POST',
        recurso: 'pedidos',
        teste: 'cenario1',
      },
    });

    check(res, {
      'pedido criado(cenario 1) (200 ou 201)': (r) => r.status === 200 || r.status === 201,
    }, {
      recurso: 'pedidos',
    });
  });

  sleep(1); // pausa para simular tempo de uso real
}