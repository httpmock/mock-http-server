package com.github.httpmock.request;

import static com.github.httpmock.util.CollectionUtil.emptyMap;

import java.util.Map;

import com.github.httpmock.MockReplayListener;
import com.github.httpmock.dto.ConfigurationDto;
import com.github.httpmock.dto.RequestDto;

public class RequestCounter implements MockReplayListener {

	private Map<RequestDto, Integer> counts;

	public RequestCounter() {
		counts = emptyMap();
	}

	public void count(RequestDto request) {
		if (counts.get(request) == null) {
			counts.put(request, 1);
		} else {
			counts.put(request, counts.get(request) + 1);
		}
	}

	public int getCount(RequestDto requestDto) {
		Integer num = counts.get(requestDto);
		if (num == null)
			return 0;
		return num;
	}

	@Override
	public void onReplay(ConfigurationDto configuration) {
		count(configuration.getRequest());
	}
}
