import http from 'k6/http';
import { check, sleep } from 'k6';
import { group } from 'k6';
import { randomString, randomInt } from 'https://jslib.k6.io/k6-utils/1.4.0/index.js';

export const options = {
  vus: 100,
  iterations: 1000, // Vai criar 1000 vezes (ajuste conforme necessário)
  // stages: [
  // { duration: '1m', target: 5 },   // sobe para 5 VUs em 1 minuto
  // { duration: '3m', target: 5 },   // mantém 5 VUs por 3 minutos
  // { duration: '1m', target: 10 },  // sobe para 10 VUs em 1 minuto
  // { duration: '3m', target: 10 },  // mantém 10 VUs por 3 minutos
  // { duration: '1m', target: 0 },   // desce para 0 VUs em 1 minuto
  // ],
  thresholds: {
    http_req_duration: ['p(95)<500'], // 95% das requisições devem responder em menos de 500ms
    'http_req_failed{tipo:cliente}': ['rate<0.01'], // menos de 1% de falhas para cliente
    'http_req_failed{tipo:produto}': ['rate<0.01'], // menos de 1% de falhas para produto
  },
  tags: { teste: "cenario0" },
};

const BASE_URL = 'http://10.255.255.254:8080';
const CLIENTE_API_PATH = '/api/clientes';
const PRODUTO_API_PATH = '/api/produtos';
const PEDIDO_API_PATH = '/api/pedidos';

function randomFloat(min, max, decimals) {
  const str = (Math.random() * (max - min) + min).toFixed(decimals);
  return parseFloat(str);
}

function generateClientPayload() {
  return JSON.stringify({
    nome: `Cliente ${randomString(6)}`,
    email: `${randomString(10)}@example.com`,
  });
}

function generateProductPayload() {
  return JSON.stringify({
    nome: `Produto ${randomString(8)}`,
    preco: randomFloat(10, 500, 2),
  });
}


export default function () {
  const headers = { 'Content-Type': 'application/json' };
  //let resCliente; 

  // Criar cliente
  group('Criar cliente', () => {
    const resCliente = http.post(`${BASE_URL}${CLIENTE_API_PATH}`, generateClientPayload(), { headers });
    check(resCliente, { 'cliente criado (cenario0)': (r) => r.status === 200 }, { tipo: 'cliente' });
  });
  //const clienteId = JSON.parse(resCliente.body).id;

  // Criar produtos (2 por iteração)
  const produtoIds = [];

  group('Criar produtos', () => {
    for (let i = 0; i < 2; i++) {
      const resProduto = http.post(`${BASE_URL}${PRODUTO_API_PATH}`, generateProductPayload(), { headers });
      check(resProduto, { 'produto criado (cenario0)': (r) => r.status === 200 }, { tipo: 'produto' });

      const produtoId = JSON.parse(resProduto.body).id;
      produtoIds.push(produtoId);
    }
  });


  sleep(0.1); // pequena pausa para não sobrecarregar
}