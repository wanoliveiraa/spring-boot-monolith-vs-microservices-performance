import http from 'k6/http';
import { check, sleep } from 'k6';

function randomInt(min, max) {
  return Math.floor(Math.random() * (max - min + 1)) + min;
}

export const options = {
  stages: [
    { duration: '10s', target: 10 },   // subida inicial (baixa carga)
    { duration: '5s', target: 500 },   // pico repentino
    { duration: '10s', target: 500 },  // mantém o pico
    { duration: '5s', target: 10 },    // queda brusca
    { duration: '10s', target: 10 },   // estabiliza novamente
  ],
  thresholds: {
    'http_req_failed{recurso:pedidos}': ['rate<0.05'], // tolera até 5% de erro
    'http_req_duration{recurso:pedidos}': ['p(95)<600'],
    'checks{recurso:pedidos}': ['rate>0.98'],
  },
  tags: { teste: 'cenario5' },
};

const BASE_URL = 'http://10.255.255.254:8080';
const PEDIDO_API_PATH = '/api/pedidos';
const TOTAL_PEDIDOS = 1000;

export default function () {
  const pedidoId = randomInt(1, TOTAL_PEDIDOS);

  const res = http.get(`${BASE_URL}${PEDIDO_API_PATH}/${pedidoId}`, {
    tags: {
      tipo: 'GET',
      recurso: 'pedidos',
      teste: 'cenario5'
    }
  });

  check(res, {
    'pedido retornado com sucesso (200)(cenario5)': (r) => r.status === 200,
  }, {
    recurso: 'pedidos',
  });

  sleep(1);
}
