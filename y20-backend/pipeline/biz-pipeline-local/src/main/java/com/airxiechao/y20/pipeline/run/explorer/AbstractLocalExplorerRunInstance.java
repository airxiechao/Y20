package com.airxiechao.y20.pipeline.run.explorer;

import com.airxiechao.axcboot.storage.fs.IFs;
import com.airxiechao.axcboot.storage.fs.common.FsFile;
import com.airxiechao.axcboot.util.StreamUtil;
import com.airxiechao.axcboot.util.UuidUtil;
import com.airxiechao.axcboot.util.function.TriConsumer;
import com.airxiechao.y20.pipeline.pojo.FileHolder;
import com.airxiechao.y20.pipeline.pojo.FilePart;

import java.io.InputStream;
import java.io.OutputStream;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

public abstract class AbstractLocalExplorerRunInstance implements IExplorerRunInstance {

    private String explorerRunInstanceUuid;
    private IFs fs;
    private Map<String, FileHolder> holderMap = new ConcurrentHashMap<>();

    public AbstractLocalExplorerRunInstance(IFs fs){
        this.explorerRunInstanceUuid = UuidUtil.random();
        this.fs = fs;
    }

    @Override
    public FsFile getFile(String path) throws Exception{
        return fs.get(path);
    }

    @Override
    public List<FsFile> list(String path) throws Exception{
        return fs.list(path);
    }

    @Override
    public void create() throws Exception{

    }

    @Override
    public void destroy() throws Exception{

    }

    @Override
    public void download(String path, OutputStream outputStream, TriConsumer<Long, Long, Double> totalSpeedProgressConsumer) throws Exception {
        try(InputStream inputStream = fs.getInputStream(path)){
            StreamUtil.readInputToOutputStream(inputStream, 1024, outputStream);
        }
    }

    @Override
    public void stopDownload(String path){

    }

    @Override
    public void upload(String path, long size, Long maxSpeed, InputStream inputStream, TriConsumer<Long, Long, Double> totalSpeedProgressConsumer) throws Exception {
        try(OutputStream outputStream = fs.getOutputStream(path)){
            StreamUtil.readInputToOutputStream(inputStream, 1024, outputStream);
        }
    }

    @Override
    public void stopUpload(String path){

    }

    @Override
    public String createFileHolder(String path, long size) throws Exception{
        FileHolder holder = new FileHolder(path, size);
        boolean created = fs.mkdirs(holder.getHolderPath());
        if(!created){
            throw new Exception("create file holder error");
        }

        holderMap.put(holder.getId(), holder);
        return holder.getId();
    }

    @Override
    public boolean pushFilePart(String holderId, long pos, byte[] bytes) throws Exception {
        FileHolder holder = holderMap.get(holderId);
        if(null == holder){
            throw new Exception("no file holder");
        }

        String partPath = holder.getPartPath(pos, bytes.length);
        try(OutputStream outputStream = fs.getOutputStream(partPath)){
            outputStream.write(bytes);
        }

        return true;
    }

    @Override
    public String saveFileHolder(String holderId) throws Exception {
        FileHolder holder = holderMap.get(holderId);
        if(null == holder){
            throw new Exception("no file holder");
        }

        List<FsFile> parts = fs.list(holder.getHolderPath());

        // check parts
        List<FilePart> fileParts = parts.stream()
                .map(p -> new FilePart(p.getName()))
                .sorted((l, r) -> l.getPos() > r.getPos() ? 1 : -1)
                .collect(Collectors.toList());

        long pos = 0;
        for(FilePart part : fileParts){
            if(pos != part.getPos()){
                throw new Exception("holder broken");
            }

            pos += part.getLength();
        }

        if(pos != holder.getSize()){
            throw new Exception("holder broken");
        }

        // merge parts
        String path = holder.getPath();
        try(OutputStream outputStream = fs.getOutputStream(path)){
            for(FilePart part : fileParts){
                try(InputStream inputStream = fs.getInputStream(holder.getPartPath(part.getPos(), part.getLength()))){
                    outputStream.write(inputStream.readAllBytes());
                    outputStream.flush();
                }
            }
        }

        removeFileHolder(holderId);
        return path;
    }

    @Override
    public boolean removeFileHolder(String holderId) throws Exception {
        FileHolder holder = holderMap.get(holderId);
        if(null == holder){
            throw new Exception("no file holder");
        }

        return fs.remove(holder.getHolderPath());
    }

    @Override
    public byte[] pullFilePart(String path, long pos, int length) throws Exception {
        try(InputStream inputStream = fs.getInputStream(path)){
            inputStream.skip(pos);
            return inputStream.readNBytes(length);
        }
    }

    @Override
    public String getExplorerRunInstanceUuid() {
        return explorerRunInstanceUuid;
    }
}