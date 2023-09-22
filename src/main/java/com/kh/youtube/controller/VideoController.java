package com.kh.youtube.controller;

import com.kh.youtube.domain.*;
import com.kh.youtube.service.CommentLikeService;
import com.kh.youtube.service.VideoCommentService;
import com.kh.youtube.service.VideoLikeService;
import com.kh.youtube.service.VideoService;
import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.dsl.BooleanExpression;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.UUID;


@RestController
@RequestMapping("/api/*")
@Log4j2
@CrossOrigin(origins = {"*"}, maxAge = 6000)
public class VideoController {

    @Value("${youtube.upload.path}")// application.properties에 있는 변수 // 롬복말고 spring꺼임
    private String uploadPath;

    @Autowired
    private VideoService videoService;
    @Autowired
    private VideoLikeService videoLike;
    @Autowired
    private VideoCommentService videoComment;
    @Autowired
    private CommentLikeService commentLike;

    // 영상 전체 조회 GET http://localhost:8080/api/video
    @GetMapping("/video")
    public ResponseEntity <List<Video>> showAllVideo(@RequestParam(name = "page", defaultValue = "1")
        int page, @RequestParam(name="category", required = false) Integer category){
        // @RequestParam은 원랜 명시 안해도 되지만 저런 경우엔 명시 돼야함

        // 페이지 넘버는 계속 바껴야 하니 parameter로 받아옴 defaultValue "1"로 첫번째 페이지 고정 해야 page-1이 처리가 됨

        // 디폴트 값으로 지정해 두면  http://localhost:8080/api/video?page=(몇번째 페이지) 이런 식으로 안쓰고

        // http://localhost:8080/api/video 이렇게 바로 첫 페이지가 나옴


        // 정렬 기능
        // by = 무엇을 기준 으로 할건지 ex) code id 등
        Sort sort = Sort.by("videoCode").descending();


        // 한 페이지의 10개 첫번 째는 페이지는 0부터 시작 두번 째는 한 페이지에 몇개가 들어올 건지
        Pageable pageable = PageRequest.of(page-1, 20, sort); // 두번째 페이지는 1부터

        //http://localhost:8080/api/video?page=  : 페이징 처리후 포스트맨에서 확인할때

        // 동적 쿼리를 위한 QueryDSL을 사용한 코드들을 추가함

        // 1. Q도메인 클래스를 가져와야 한다.
        QVideo qVideo = QVideo.video;
        
        // 2. BooleanBuilder는 where문에 들어가는 조건들을 넣어주는 컨테이너 지정
        BooleanBuilder builder = new BooleanBuilder();

        if(category!=null){
            // 3. 원하는 조건은 필드값과 같이 결합해서 생성한다. eq= 문자열끼리 일치시킬때
            BooleanExpression expression =qVideo.category.categoryCode.eq(category);

            // 4. 만들어진 조건은 where문에 and나 or 같은 키워드와 결합한다.
            builder.and(expression);
        }
        Page<Video> result = videoService.showAll(pageable, builder); // Service 가서도 Pageable, builder를 넣어야함


//        log.info("Total Pages : " + result.getTotalPages()); // 총 몇 페이지인지
//        log.info("Total Count : " + result.getTotalElements()); // 전체 몇개인지
//        log.info("Page Number : " + result.getNumber()); // 현재 페이지 번호
//        log.info("Page Size : " + result.getSize()); // 페이지 당 데이터 개수
//        log.info("Next Page : " + result.hasNext()); // 다음 페이지가 있는지 존재 여부 확인
//        log.info("First Page : "+result.isFirst()); // 시작 페이지 여부
        
        return ResponseEntity.status(HttpStatus.OK).body(result.getContent()); // Page<Video>이기 때문에 List인 getContent로 리턴 해야함
   //     return ResponseEntity.status(HttpStatus.OK).build();
    }
    
    // 영상 추가 POST http://localhost:8080/api/video
    @PostMapping ("/video")
    public ResponseEntity<Video> createVideo(MultipartFile video, MultipartFile image, @RequestParam(name="desc", required = false) String title, String desc,String categoryCode){
                                                    // 필수 값이 아닌건 @RequestParam 걸어둘 수 있음 여러 개는 각각 에다 지정 가능
        log.info("video : " + video);
        log.info("image : " + image);
        log.info("title : " + title);
        log.info("desc : " + desc);
        log.info("categoryCode : " + categoryCode);
        // video_title, video_desc, video_url, video_photo, category_code

        // 업로드 처리
        // 비디오의 실제 파일 이름
        String originalVideo = video.getOriginalFilename();
        // 파일이름에 경로가 같이 붙을 경우가 있어서 방지 차원에서 미리 잘라버림
        String realVideo = originalVideo.substring(originalVideo.lastIndexOf("\\")+1);
        log.info("realVideo : " + realVideo);

        // UUID 랜덤 아이디 부여
        String uuid = UUID.randomUUID().toString();

        // 실제로 저장할 파일 명 (위치 포함)
        String saveVideo = uploadPath + File.separator + uuid + "_" + realVideo;
        Path pathVideo = Paths.get(saveVideo);
        try {
            video.transferTo(pathVideo);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        // 이미지의 실제 파일 이름
        String originalImage = image.getOriginalFilename();
        // 파일이름에 경로가 같이 붙을 경우가 있어서 방지 차원에서 미리 잘라버림
        String realImage = originalImage.substring(originalImage.lastIndexOf("\\")+1);
        String saveImage = uploadPath + File.separator + uuid + "_" + realImage;
        Path pathImage = Paths.get(saveImage);

        try {
            image.transferTo(pathImage);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        // 파일 업로드가 끝났으니 경로 (savaPhoto), name, desc, memberId(id) 설정해서 넣는것

        Video vo = new Video();

        vo.setVideoTitle(title);
        vo.setVideoDesc(desc);
        vo.setVideoUrl(uuid + "_" + realVideo);
        vo.setVideoPhoto(uuid + "_" + realImage);

        Category category = new Category();
        category.setCategoryCode(Integer.parseInt(categoryCode));
        vo.setCategory(category);

        Channel channel = new Channel();
        channel.setChannelCode(43);
        vo.setChannel(channel);

        Member member = new Member();
        member.setId("user1");
        vo.setMember(member);

        //return  ResponseEntity.status(HttpStatus.OK).build();
       return ResponseEntity.status(HttpStatus.OK).body(videoService.create(vo));

    }

    // 영상 수정 PUT http://localhost:8080/api/video
    @PutMapping("/video")
    public ResponseEntity<Video> updateVideo(@RequestBody Video vo){
        return ResponseEntity.status(HttpStatus.OK).body(videoService.update(vo));
    }
    
    // 영상 삭제 DELETE http://localhost:8080/api/video/1 <<-비디오 코드
    @PutMapping("/video/{id}")
    public ResponseEntity<Video> deleteVideo(@PathVariable int id){
        return ResponseEntity.status(HttpStatus.OK).body(videoService.delete(id));
    }
    // 영상 1개 조회 GET http://localhost:8080/api/video/1
    @GetMapping("/video/{id}")
    public ResponseEntity<Video> showVideo (@PathVariable int id){
        return ResponseEntity.status(HttpStatus.OK).body(videoService.show(id));
    }
    
    // 영상 1개에 따른 댓글 전체 조회 GET http://localhost:8080/api/video/1/comment
    @GetMapping("/video/{id}/comment")
    public ResponseEntity <List<VideoComment>> videoCommentList(@PathVariable int code){
        return ResponseEntity.status(HttpStatus.OK).body(videoComment.findByVideoCode(code));
    }

    // 좋아요 추가 (수정,생성) POST http://localhost:8080/api/video/like
    @PostMapping("/video/like")
    public ResponseEntity<VideoLike> createVideoLike(@RequestBody VideoLike vo){
        return ResponseEntity.status(HttpStatus.OK).body(videoLike.create(vo));
    }

    // 좋아요 취소 (삭제) DELETE http://localhost:8080/api/video/like/1
    @DeleteMapping ("/video/like/{code}")
    public ResponseEntity<VideoLike> createVideoLike(@PathVariable int id){
        return ResponseEntity.status(HttpStatus.OK).body(videoLike.delete(id));
    }


    // 댓글 추가 (수정,생성) POST http://localhost:8080/api/video/comment
    @PostMapping("/video/comment")
    public ResponseEntity<VideoComment> createVideoComment(@RequestBody VideoComment vo){
        return ResponseEntity.status(HttpStatus.OK).body(videoComment.create(vo));
    }

    // 댓글 수정 PUT http://localhost:8080/api/video/comment
    @PutMapping ("/video/comment")
    public ResponseEntity<VideoComment> updateVideoComment(@RequestBody VideoComment vo){
        return ResponseEntity.status(HttpStatus.OK).body(videoComment.update(vo));
    }

    // 댓글 삭제 DELETE http://localhost:8080/api/video/comment/
    @DeleteMapping ("/video/comment")
    public ResponseEntity<VideoComment> deleteVideoComment(@PathVariable int id){
        return ResponseEntity.status(HttpStatus.OK).body(videoComment.delete(id));
    }


    // 댓글 좋아요 추가 (수정,생성) POST http://localhost:8080/api/video/comment/like
    @PostMapping ("/video/comment/like")
    public ResponseEntity<CommentLike> createCommentLike(@RequestBody CommentLike vo){
        return ResponseEntity.status(HttpStatus.OK).body(commentLike.create(vo));
    }

    // 댓글 좋아요 취소 (삭제) DELETE http://localhost:8080/api/video/comment/like/1
    @DeleteMapping("/video/comment/like/{code}")
    public ResponseEntity<CommentLike> deleteCommentLike(@PathVariable int id){
        return ResponseEntity.status(HttpStatus.OK).body(commentLike.delete(id));
    }
}

