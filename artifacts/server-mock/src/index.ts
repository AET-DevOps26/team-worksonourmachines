import cors from 'cors';
import express from 'express';
import { marketplaceRouter } from './routes/marketplace.js';
import { studentRouter } from './routes/student.js';

const app = express();
const port = Number(process.env.PORT ?? 3000);

app.use(cors());
app.use(express.json());

app.get('/health', (_req, res) => {
  res.json({ status: 'ok' });
});

app.use('/v1', studentRouter);
app.use('/v1', marketplaceRouter);

app.listen(port, () => {
  console.log(`server-mock listening on port ${port}`);
});
