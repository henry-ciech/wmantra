import dotenv from 'dotenv';
import express from 'express';
import puppeteer from 'puppeteer';

dotenv.config();

const app = express();
const screenshotterPort = process.env.SCREENSHOTTER_PORT || 8084;
const templaterPort = process.env.TEMPLATER_PORT || 8080;

app.get('/take-screenshot', async (req, res) => {
    const longitude = req.query.longitude || 'longitude';
    const latitude = req.query.latitude || 'latitude';

    try {
        console.log('Received a request');
        const browser = await puppeteer.launch({
            args: ['--no-sandbox', '--disable-setuid-sandbox', '--disable-dev-shm-usage'],
            headless: true, // Corrected from 'new' to true
            executablePath: process.env.PUPPETEER_EXECUTABLE_PATH
        });
        const page = await browser.newPage();
        await page.setViewport({
            width: 540,
            height: 620,
            deviceScaleFactor: 7,
        });

        const url = `http://templater:${templaterPort}/?longitude=${longitude}&latitude=${latitude}`;
        console.log('Sending a request to the templater:', url);
        await page.goto(url, {
            waitUntil: 'networkidle0',
            timeout: 30000
        });

        const screenshot = await page.screenshot({ type: 'png' });
        console.log('Screenshot done, sending photo');
        await browser.close();

        res.type('image/png').send(screenshot);
        console.log('Photo sent');
    } catch (error) {
        console.error('Error when making screenshot:', error.message);
        res.status(500).send('Error taking screenshot');
    }
});

app.listen(screenshotterPort, () => {
    console.log(`Server running at http://localhost:${screenshotterPort}`);
});
