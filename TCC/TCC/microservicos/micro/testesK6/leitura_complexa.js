import http from 'k6/http';
import { check, sleep, group } from 'k6';

export const options = {
  vus: 1000,
  duration: '1m',
  thresholds: {
    'http_req_failed{recurso:pedidos}': ['rate<0.01'],
    'http_req_duration{recurso:pedidos}': ['p(95)<400'],
    'checks{recurso:pedidos}': ['rate>0.99'],
  },
  tags: { teste: 'cenario3' },
};

const BASE_URL = 'http://10.255.255.254:8080';
const PEDIDOS_POR_CLIENTE_API_PATH = '/api/pedidos/cliente';
const TOTAL_CLIENTES = 1000;

function randomInt(min, max) {
  return Math.floor(Math.random() * (max - min + 1)) + min;
}

export default function () {
  group('Buscar pedidos por cliente', () => {
    const clienteId = randomInt(1, TOTAL_CLIENTES);

    const res = http.get(`${BASE_URL}${PEDIDOS_POR_CLIENTE_API_PATH}/${clienteId}`, {
      tags: { tipo: 'GET', recurso: 'pedidos', teste: 'cenario3' },
    });

    check(res, {
      'pedido consulta complexo obtido 200 (cenario3)': (r) => r.status === 200,
      'pedido consulta complexo contém lista (cenario3)': (r) => {
        try {
          const body = JSON.parse(r.body);
          return Array.isArray(body);
        } catch {
          return false;
        }
      },
    }, {
      recurso: 'pedidos',
    });
  });

  sleep(1);
}
