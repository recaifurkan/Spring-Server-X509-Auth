var axios = require('axios');
const fs = require('fs');
const https = require('https');
const httpsAgent = new https.Agent({
  pfx: fs.readFileSync("../server/store/clientRecai.p12"),
  passphrase: "changeit",
  rejectUnauthorized: false,
})

var config = {
  method: 'get',
  url: 'https://localhost:8443/user',
  headers: {
  },
  httpsAgent
};

axios(config)
  .then(function (response) {
    console.log(JSON.stringify(response.data));
  })
  .catch(function (error) {
    console.log(error);
  });
