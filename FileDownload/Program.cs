﻿namespace FileDownload;

class Program
{
    public static async Task Main(string[] args)
    {
        var cancellationToken = new CancellationTokenSource().Token;
        var peer = new Peer();

        var task = peer.Start(cancellationToken);

        if (args.Length > 0 && args[0] == "download")
        {
            await peer.DownloadFileAsync(args[1], int.Parse(args[2]), args[3], args[4], cancellationToken);
        }
        else
        {
            Console.WriteLine("Waiting for other peers...");
        }
        await task;
    }
}