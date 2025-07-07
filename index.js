const qrcode = require('qrcode-terminal');
const { Client, LocalAuth } = require('whatsapp-web.js');
const axios = require('axios');
const express = require('express');

const client = new Client({
    authStrategy: new LocalAuth() // Simpan session di folder .wwebjs_auth
});

const app = express();
app.use(express.json());

client.on('qr', qr => {
    qrcode.generate(qr, { small: true });
});

client.on('ready', () => {
    console.log('✅ WhatsApp bot is ready!');
});

client.on('message', async msg => {
    const userMsg = msg.body;

    try {
        const res = await axios.post('http://localhost:8081/api/chat/bot', {
            message: userMsg
        });

        const reply = res.data.reply || "Maaf, saya tidak bisa menjawab.";
        msg.reply(reply);
    } catch (err) {
        console.error("Gagal jawab:", err);
        msg.reply("Bot error 😵");
    }
});

client.initialize();
