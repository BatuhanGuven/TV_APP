const express = require('express');
const multer = require('multer');
const path = require('path');
const fs = require('fs');
const mkdirp = require('mkdirp');
const os = require('os');

const app = express();


function getServerIp() {
  const interfaces = os.networkInterfaces();
  for (const name of Object.keys(interfaces)) {
    for (const interface of interfaces[name]) {
      const { family, address, internal } = interface;
      if (family === 'IPv4' && !internal) {
        return address; 
      }
    }
  }
  return 'localhost'; 
}

const serverIp = getServerIp();


mkdirp.sync('uploads/videos');
mkdirp.sync('uploads/uiconfig');
mkdirp.sync('uploads/logos');


const videoStorage = multer.diskStorage({
  destination: function (req, file, cb) {
    cb(null, 'uploads/videos/');
  },
  filename: function (req, file, cb) {
    cb(null, Date.now() + path.extname(file.originalname));
  }
});

const videoUpload = multer({ storage: videoStorage });

const uiConfigStorage = multer.diskStorage({
  destination: function (req, file, cb) {
    cb(null, 'uploads/uiconfig/');
  },
  filename: function (req, file, cb) {
    cb(null, 'uiconfig.json');
  }
});

const uiConfigUpload = multer({ storage: uiConfigStorage });

const logoStorage = multer.diskStorage({
  destination: function (req, file, cb) {
    cb(null, 'uploads/logos/');
  },
  filename: function (req, file, cb) {
    cb(null, 'logo' + path.extname(file.originalname));
  }
});

const logoUpload = multer({ storage: logoStorage });

app.post('/upload/video', videoUpload.single('video'), (req, res) => {
  if (!req.file) {
    return res.status(400).send('No video file uploaded.');
  }
  const videoUrl = `http://${serverIp}:3000/uploads/videos/${req.file.filename}`;
  res.send({ videoUrl });
});

app.post('/upload/uiconfig', uiConfigUpload.single('uiconfig'), (req, res) => {
  if (!req.file) {
    return res.status(400).send('No UI configuration file uploaded.');
  }
  res.send({ message: 'UI configuration uploaded successfully.' });
});

app.post('/upload/logo', logoUpload.single('logo'), (req, res) => {
  if (!req.file) {
    return res.status(400).send('No logo file uploaded.');
  }
  const logoUrl = `http://${serverIp}:3000/uploads/logos/${req.file.filename}`;
  res.send({ logoUrl });
});


app.get('/uploads/videos', (req, res) => {
  fs.readdir('uploads/videos', (err, files) => {
    if (err) {
      return res.status(500).send('Unable to scan directory');
    }
    const videoFiles = files.map(file => ({
      url: `http://${serverIp}:3000/uploads/videos/${file}`,
      name: file
    }));
    res.json(videoFiles);
  });
});

app.get('/uploads/uiconfig', (req, res) => {
  const uiConfigPath = 'uploads/uiconfig/uiconfig.json';
  fs.readFile(uiConfigPath, 'utf8', (err, data) => {
    if (err) {
      return res.status(500).send('Unable to read UI configuration file.');
    }
    res.json(JSON.parse(data));
  });
});

app.get('/logo', (req, res) => {
  const logoPath = 'uploads/logos/logo.png';
  fs.access(logoPath, fs.constants.F_OK, (err) => {
    if (err) {
      return res.status(404).send('Logo not found.');
    }
    res.sendFile(path.resolve(logoPath));
  });
});


app.use('/uploads/videos', express.static('uploads/videos'));
app.use('/uploads/logos', express.static('uploads/logos'));


app.listen(3000, () => {
  console.log(`Server is running on http://${serverIp}:3000`);
});
