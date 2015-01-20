package com.github.httpmock;

import com.github.httpmock.dto.ConfigurationDto;

public interface MockReplayListener {
    void onReplay(ConfigurationDto configuration);
}
