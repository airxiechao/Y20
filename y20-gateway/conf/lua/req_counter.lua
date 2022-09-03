local num_req = 0

local _M = {}

function _M.incoming()
  if num_req < 0 then
    num_req = 1
  else
    num_req = num_req + 1
  end
end

function _M.leaving()
  if num_req > 0 then
    num_req = num_req - 1
  end
end

function _M.count()
  if num_req > 0 then
    return num_req
  else
    return 0
  end
end

return _M