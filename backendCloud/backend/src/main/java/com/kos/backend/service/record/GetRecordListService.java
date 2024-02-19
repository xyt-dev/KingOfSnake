package com.kos.backend.service.record;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;

public interface GetRecordListService {
    JSONObject getList(Integer page);
}
