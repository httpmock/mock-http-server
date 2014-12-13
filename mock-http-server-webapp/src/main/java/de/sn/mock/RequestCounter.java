package de.sn.mock;

import static de.sn.mock.util.CollectionUtil.emptyMap;

import java.util.Map;

import de.sn.mock.dto.RequestDto;

public class RequestCounter {

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

}
