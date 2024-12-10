package com.pustovalov.cloudstorage.controller;

import com.pustovalov.cloudstorage.dto.CreateFolderDto;
import com.pustovalov.cloudstorage.dto.DeleteFolderDto;
import com.pustovalov.cloudstorage.dto.GetContentDto;
import com.pustovalov.cloudstorage.dto.RenameFolderDto;
import com.pustovalov.cloudstorage.dto.request.CreateFolderRequest;
import com.pustovalov.cloudstorage.dto.request.RenameFolderRequest;
import com.pustovalov.cloudstorage.service.FolderService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.security.Principal;
import java.util.List;

import static com.pustovalov.cloudstorage.utils.PathUtils.parseName;
import static com.pustovalov.cloudstorage.utils.PathUtils.parsePath;

@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping("/folders")
public class FolderController {

    private final FolderService folderService;

    @PostMapping("/**")
    public ResponseEntity<String> create(@RequestBody CreateFolderRequest createFolderRequest, HttpServletRequest request,
                                         Principal principal) {
        // POST http://localhost:8080/folders/someDir создает /username/someDir/nameFromBody с переданным в теле именем
        String folderPath = parsePath(request.getRequestURI());
        folderService.saveFolderMetaData(
                new CreateFolderDto(principal.getName(), createFolderRequest.folderName(), folderPath));

        return ResponseEntity.created(URI.create(""))
                .build();
    }

    @GetMapping("/**")
    public ResponseEntity<List<String>> getContent(HttpServletRequest request, Principal principal) {
        // GET http://localhost:8080/folders/a отображает вложенные папки внутри /username/a/
        String folderPath = parsePath(request.getRequestURI());
        List<String> content = folderService.getContent(new GetContentDto(folderPath, principal.getName()));

        return ResponseEntity.ok(content);
    }

    @DeleteMapping("/**")
    public ResponseEntity<String> delete(HttpServletRequest request, Principal principal) {
        // DELETE http://localhost:8080/folders/a/b должен удалить текущую и подкаталоги
        String requestURI = request.getRequestURI();
        String folderName = parseName(request.getRequestURI());
        String folderPath = parsePath(requestURI, folderName);
        folderService.delete(new DeleteFolderDto(folderName, folderPath, principal.getName()));

        return ResponseEntity.ok().build();
    }

    @PatchMapping("/**")
    public ResponseEntity<String> rename(@RequestBody RenameFolderRequest renameFolderRequest, HttpServletRequest request,
                                         Principal principal) {
        String requestURI = request.getRequestURI();
        String folderName = parseName(request.getRequestURI());
        String folderPath = parsePath(requestURI, folderName);

        folderService.rename(RenameFolderDto.builder()
                                     .name(folderName)
                                     .path(folderPath)
                                     .newName(renameFolderRequest.newName())
                                     .username(principal.getName())
                                     .build());

        return ResponseEntity.ok().build();
    }

}
