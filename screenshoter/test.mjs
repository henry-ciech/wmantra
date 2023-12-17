import express from 'express';
import puppeteer from 'puppeteer';

const app = express();
const port = 8084;

app.get('/take-screenshot', async (req, res) => {
    // Extract longitude and latitude from the query
    const longitude = req.query.longitude || 'longitude';
    const latitude = req.query.latitude || 'latitude';

    try {
        const browser = await puppeteer.launch();
        const page = await browser.newPage();
        await page.setViewport({
            width: 540,
            height: 620,
            deviceScaleFactor: 7,
        });

        // Pass the longitude and latitude to the URL
        await page.goto(`http://localhost:8080/?longitude=${longitude}&latitude=${latitude}`, {waitUntil: 'networkidle0'});

        const screenshot = await page.screenshot({
            fullPage: true,
            type: 'png'  // Changed from 'jpeg' to 'png'
            // Removed the 'quality' option as it is not applicable for PNG
        });
        await browser.close();

        res.type('image/png').send(screenshot);  // Set response type to 'image/png'
    } catch (error) {
        console.error('Error taking screenshot:', error);
        res.status(500).send('Error taking screenshot');
    }
});

app.listen(port, () => {
    console.log(`Server running at http://localhost:${port}`);
});
