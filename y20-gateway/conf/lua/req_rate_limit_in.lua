req_counter = require("req_counter")
req_counter.incoming()

local count = req_counter.count()

if count > config.max_download_req then
  ngx.var.limit_rate = '10k'
end
