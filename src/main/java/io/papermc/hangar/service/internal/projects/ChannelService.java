package io.papermc.hangar.service.internal.projects;

import io.papermc.hangar.db.dao.HangarDao;
import io.papermc.hangar.db.dao.internal.HangarProjectsDAO;
import io.papermc.hangar.db.dao.internal.table.projects.ProjectChannelsDAO;
import io.papermc.hangar.exceptions.HangarApiException;
import io.papermc.hangar.model.common.Color;
import io.papermc.hangar.model.db.projects.ProjectChannelTable;
import io.papermc.hangar.model.internal.projects.HangarChannel;
import io.papermc.hangar.service.HangarService;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class ChannelService extends HangarService {

    private final ProjectChannelsDAO projectChannelsDAO;
    private final HangarProjectsDAO hangarProjectsDAO;

    public ChannelService(HangarDao<ProjectChannelsDAO> projectChannelsDAO, HangarDao<HangarProjectsDAO> hangarProjectsDAO) {
        this.projectChannelsDAO = projectChannelsDAO.get();
        this.hangarProjectsDAO = hangarProjectsDAO.get();
    }

    private void validateChannel(String name, Color color, long projectId, boolean nonReviewed, List<ProjectChannelTable> existingChannels) {
        if (!config.channels.isValidChannelName(name)) {
            throw new HangarApiException(HttpStatus.BAD_REQUEST, "channel.modal.error.invalidName");
        }

        if (existingChannels.size() >= config.projects.getMaxChannels()) {
            throw new HangarApiException(HttpStatus.BAD_REQUEST, "channel.modal.error.maxChannels", config.projects.getMaxChannels());
        }

        if (existingChannels.stream().anyMatch(ch -> ch.getColor() == color)) {
            throw new HangarApiException(HttpStatus.BAD_REQUEST, "channel.modal.error.duplicateColor");
        }

        if (existingChannels.stream().anyMatch(ch -> ch.getName().equalsIgnoreCase(name))) {
            throw new HangarApiException(HttpStatus.BAD_REQUEST, "channel.modal.error.duplicateName");
        }
    }

    public ProjectChannelTable createProjectChannel(String name, Color color, long projectId, boolean nonReviewed) {
        validateChannel(name, color, projectId, nonReviewed, projectChannelsDAO.getProjectChannels(projectId));
        return projectChannelsDAO.insert(new ProjectChannelTable(name, color, projectId, nonReviewed));
    }

    public void editProjectChannel(long channelId, String name, Color color, long projectId, boolean nonReviewed) {
        ProjectChannelTable projectChannelTable = getProjectChannel(channelId);
        if (projectChannelTable == null) {
            throw new HangarApiException(HttpStatus.NOT_FOUND);
        }
        validateChannel(name, color, projectId, nonReviewed, projectChannelsDAO.getProjectChannels(projectId).stream().filter(ch -> ch.getId() != channelId).collect(Collectors.toList()));
        projectChannelTable.setName(name);
        projectChannelTable.setColor(color);
        projectChannelTable.setNonReviewed(nonReviewed);
        projectChannelsDAO.update(projectChannelTable);
    }

    public List<HangarChannel> getProjectChannels(long projectId) {
        return hangarProjectsDAO.getHangarChannels(projectId);
    }

    public ProjectChannelTable getProjectChannel(long projectId, String name, Color color) {
        return projectChannelsDAO.getProjectChannel(projectId, name, color);
    }

    public ProjectChannelTable getProjectChannel(long channelId) {
        return projectChannelsDAO.getProjectChannel(channelId);
    }

    public ProjectChannelTable getProjectChannelForVersion(long versionId) {
        return projectChannelsDAO.getProjectChannelForVersion(versionId);
    }

    public ProjectChannelTable getFirstChannel(long projectId) {
        return projectChannelsDAO.getFirstChannel(projectId);
    }
}
