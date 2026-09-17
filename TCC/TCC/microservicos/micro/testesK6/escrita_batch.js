import http from 'k6/http';
import { check, sleep, group } from 'k6';

// O BATCH_SIZE_K6 DEVE ser igual ao spring.jpa.properties.hibernate.jdbc.batch_size
const BATCH_SIZE_K6 = 50; 

export const options = {
    // VUS e Duration configurados para estressar a transação
    vus: 200, 
    duration: '1m',
    thresholds: {
        // Ajustamos os thresholds, pois a requisição agora move 50x mais dados
        'http_req_failed{recurso:pedidos_lote}': ['rate<0.05'], 
        'http_req_duration{recurso:pedidos_lote}': ['p(95)<2000'], // Aumentamos o p95
    },
    tags: { teste: 'cenario6' },
};

const BASE_URL = 'http://10.255.255.254:8080';
const LOTE_API_PATH = '/api/pedidos/lote'; // <--- NOVO ENDPOINT DE LOTE

const TOTAL_CLIENTES = 1000;
const TOTAL_PRODUTOS = 2000;

function randomInt(min, max) {
    return Math.floor(Math.random() * (max - min + 1)) + min;
}

// -------------------------------------------------------------------------
// NOVA FUNÇÃO: GERA UM ARRAY JSON GRANDE DE DTOs
// -------------------------------------------------------------------------
function generateBatchPayload() {
    const batch = [];
    
    // Gera 50 pedidos (o tamanho do lote)
    for (let i = 0; i < BATCH_SIZE_K6; i++) {
        const clienteId = randomInt(1, TOTAL_CLIENTES);
        const quantidadeItens = randomInt(1, 3);
        const itens = [];

        for (let j = 0; j < quantidadeItens; j++) {
            const produtoId = randomInt(1, TOTAL_PRODUTOS);
            // Formato deve casar com o DTO: { produtoId: ID }
            itens.push({ produtoId: produtoId }); 
        }
        
        // A estrutura deve casar com o DTO: { clienteId: ID, itens: [...] }
        batch.push({ 
            clienteId: clienteId, 
            itens: itens 
        });
    }

    return JSON.stringify(batch); // Retorna o JSON array: [{}, {}, ...]
}

export default function () {
    const headers = { 'Content-Type': 'application/json' };

    group('Criar lote de pedidos (Batch)', () => {
        const payload = generateBatchPayload(); // Gera o pacote de 50 pedidos

        const res = http.post(`${BASE_URL}${LOTE_API_PATH}`, payload, { // <--- POST para o ENDPOINT /lote
            headers,
            tags: {
                tipo: 'POST_BATCH',
                recurso: 'pedidos_lote', // Novo recurso para análise
                teste: 'cenario6',
            },
        });

        check(res, {
            'lote criado (200 ou 201) (cenario6)': (r) => r.status === 200 || r.status === 201,
        });
    });

    sleep(1);
}