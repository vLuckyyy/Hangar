package io.papermc.hangar.serviceold.project;

import io.papermc.hangar.db.dao.HangarDao;
import io.papermc.hangar.db.daoold.FlagDao;
import io.papermc.hangar.db.modelold.ProjectFlagsTable;
import io.papermc.hangar.model.common.projects.FlagReason;
import io.papermc.hangar.modelold.viewhelpers.ProjectFlag;
import io.papermc.hangar.serviceold.HangarService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Deprecated
@Service("flagServiceOld")
public class FlagService extends HangarService {

    private final HangarDao<FlagDao> flagDao;

    @Autowired
    public FlagService(HangarDao<FlagDao> flagDao) {
        this.flagDao = flagDao;
    }

    public boolean hasUnresolvedFlag(long projectId) {
        return flagDao.get().getUnresolvedFlag(projectId, getCurrentUser().getId()) != null;
    }

    public void flagProject(long projectId, FlagReason flagReason, String comment) {
        ProjectFlagsTable flag = new ProjectFlagsTable(
                projectId,
                getCurrentUser().getId(),
                flagReason,
                comment
        );
        flagDao.get().insert(flag);
    }

    public ProjectFlagsTable markAsResolved(long flagId, boolean resolved) {
        Long resolvedBy = resolved ? getCurrentUser().getId() : null;
        OffsetDateTime resolvedAt = resolved ? OffsetDateTime.now() : null;
        return flagDao.get().markAsResolved(flagId, resolved, resolvedBy, resolvedAt);
    }

    public List<ProjectFlag> getProjectFlags(long projectId) {
        return flagDao.get().getProjectFlags(projectId).entrySet().stream().map(entry -> entry.getKey().with(entry.getValue())).collect(Collectors.toList());
    }

    public ProjectFlag getProjectFlag(long flagId) {
        List<ProjectFlag> flags = flagDao.get().getById(flagId).entrySet().stream().map(entry -> entry.getKey().with(entry.getValue())).collect(Collectors.toList());
        if (flags.size() != 1) return null;
        return flags.get(0);
    }

    public List<ProjectFlag> getAllProjectFlags() {
        return flagDao.get().getFlags().entrySet().stream().map(entry -> entry.getKey().with(entry.getValue())).collect(Collectors.toList());
    }
}
