import http from 'k6/http';
import { check, sleep, group } from 'k6';

function randomInt(min, max) {
  return Math.floor(Math.random() * (max - min + 1)) + min;
}

export const options = {
  vus: 500,
  duration: '1m',
  thresholds: {
    'http_req_failed{recurso:pedidos}': ['rate<0.01'],
    'http_req_duration{recurso:pedidos}': ['p(95)<400'],
    'checks{recurso:pedidos}': ['rate>0.99'],
  },
  tags: { teste: 'cenario2' },
};

const BASE_URL = 'http://10.255.255.254:8080';
const PEDIDO_API_PATH = '/api/pedidos';
const TOTAL_PEDIDOS = 6000;

export default function () {
  group('Buscar pedido por ID', () => {
    const pedidoId = randomInt(1, TOTAL_PEDIDOS);

    const res = http.get(`${BASE_URL}${PEDIDO_API_PATH}/${pedidoId}`, {
      tags: { tipo: 'GET', recurso: 'pedidos', teste: 'cenario3' },
    });

    check(res, {
      'pedido consulta simples obtido (cenario2)(200)': (r) => r.status === 200,
    }, {
      recurso: 'pedidos',
    });
  });

  sleep(1);
}
