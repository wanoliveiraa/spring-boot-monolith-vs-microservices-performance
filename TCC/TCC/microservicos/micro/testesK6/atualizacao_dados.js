import http from 'k6/http';
import { check, sleep, group } from 'k6';
import { randomString } from 'https://jslib.k6.io/k6-utils/1.4.0/index.js';

export const options = {
  vus: 500,
  duration: '1m',
  thresholds: {
    'http_req_failed': ['rate<0.01'],
    'http_req_duration': ['p(95)<400'],
    'checks': ['rate>0.99'],
  },
  tags: { teste: 'cenario4' }, // tag global
};

function randomInt(min, max) {
  return Math.floor(Math.random() * (max - min + 1)) + min;
}

function randomFloat(min, max, decimals) {
  const str = (Math.random() * (max - min) + min).toFixed(decimals);
  return parseFloat(str);
}

const BASE_URL = 'http://10.255.255.254:8080';
const PEDIDO_API_PATH = '/api/pedidos';
const CLIENTE_API_PATH = '/api/clientes';
const PRODUTO_API_PATH = '/api/produtos';

const TOTAL_PEDIDOS = 1000;
const TOTAL_CLIENTES = 1000;
const TOTAL_PRODUTOS = 2000;

function getFutureISODate(daysAhead) {
  const now = new Date();
  now.setDate(now.getDate() + daysAhead);
  return now.toISOString();
}

function generateClientUpdate() {
  const clienteId = randomInt(1, TOTAL_CLIENTES);
  return JSON.stringify({
    id: clienteId,
    nome: `Cliente ${randomString(6)}`,
    email: `${randomString(10)}@example.com`,
  });
}

function generateProductUpdate() {
  const produtoId = randomInt(1, TOTAL_PRODUTOS);
  return JSON.stringify({
    id: produtoId,
    nome: `Produto ${randomString(8)}`,
    preco: randomFloat(10, 500, 2),
  });
}

function generatePedidoUpdate() {
  const pedidoId = randomInt(1, TOTAL_PEDIDOS);
  return JSON.stringify({
    id: pedidoId,
    data: getFutureISODate(3),
  });
}

export default function () {
  const headers = { 'Content-Type': 'application/json' };

  group('Atualizar pedido', () => {
    const payload = generatePedidoUpdate();
    const res = http.put(`${BASE_URL}${PEDIDO_API_PATH}`, payload, {
      headers,
      tags: { tipo: 'PUT', recurso: 'pedidos_update', teste: 'cenario4' },
    });

    check(res, {
      'pedido atualizado (cenario4) (200)': (r) => r.status === 200,
    }, { recurso: 'pedidos_mono' });
  });

  // group('Atualizar cliente', () => {
  //   const payload = generateClientUpdate();
  //   const res = http.put(`${BASE_URL}${CLIENTE_API_PATH}`, payload, {
  //     headers,
  //     tags: { tipo: 'PUT', recurso: 'clientes_mono', teste: 'cenario4' },
  //   });

  //   check(res, {
  //     'cliente atualizado (cenario4) (200)': (r) => r.status === 200,
  //   }, { recurso: 'clientes_mono' });
  // });

  // group('Atualizar produto', () => {
  //   const payload = generateProductUpdate();
  //   const res = http.put(`${BASE_URL}${PRODUTO_API_PATH}`, payload, {
  //     headers,
  //     tags: { tipo: 'PUT', recurso: 'produtos_mono', teste: 'cenario4' },
  //   });

  //   check(res, {
  //     'produto atualizado (cenario4) (200)': (r) => r.status === 200,
  //   }, { recurso: 'produtos_mono' });
  // });

  sleep(1);
}
