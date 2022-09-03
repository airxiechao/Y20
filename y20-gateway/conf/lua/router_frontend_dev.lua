resolver = require("resty.dns.resolver")

local service_name = ngx.var.service
if config.service_name ~= nil and config.service_name ~= "" then
  service_name = config.service_name
end

local query_subdomain = "y20-frontend-" .. service_name .. "-dev" .. ".service.consul"

local dnsIps = {}
table.insert(dnsIps, {"127.0.0.1", 8600})

local dns, err = resolver:new{
  nameservers = dnsIps,
  retrans = 5,
  timeout = 2000
}

if not dns then
  ngx.log(ngx.ERR, "failed to instantiate the resolver: ", err)
  ngx.exit(503)
  return
end

local records, err = dns:query(query_subdomain, {qtype = dns.TYPE_SRV})

if not records then
  ngx.log(ngx.ERR, "failed to query the DNS server: ", err)
  ngx.exit(503)
  return
end

if records.errcode then
  if records.errcode == 3 then
    ngx.log(ngx.ERR, "DNS error code #" .. records.errcode .. ": ", records.errstr)
    ngx.exit(503)
    return
  else
    ngx.log(ngx.ERR, "DNS error #" .. records.errcode .. ": ", err)
    ngx.exit(503)
    return
  end
end

local host_num = table.getn(records)
local host_index = math.random(host_num)
if records[host_index].port then
  local target_ip = dns:query(records[host_index].target)[1].address
  ngx.var.target = target_ip .. ":" .. records[host_index].port
else
  ngx.log(ngx.ERR, "DNS answer didn't include a port")
  ngx.exit(503)
end

